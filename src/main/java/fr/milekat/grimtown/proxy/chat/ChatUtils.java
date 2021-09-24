package fr.milekat.grimtown.proxy.chat;

import fr.milekat.grimtown.MainBungee;
import fr.milekat.grimtown.event.features.classes.Team;
import fr.milekat.grimtown.proxy.chat.classes.Message;
import fr.milekat.grimtown.proxy.chat.manager.MessageManager;
import fr.milekat.grimtown.proxy.core.CoreUtils;
import fr.milekat.grimtown.proxy.core.classes.Profile;
import fr.milekat.grimtown.proxy.core.manager.ProfileManager;
import fr.milekat.grimtown.proxy.moderation.classes.Mute;
import fr.milekat.grimtown.proxy.moderation.managers.MuteManager;
import fr.milekat.grimtown.utils.RabbitMQ;
import fr.milekat.utils.DateMileKat;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

public class ChatUtils {
    public static void sendMessages(int count) {
        MessageManager.getLast(count).forEach(ChatUtils::sendMessage);
    }

    /**
     * Send (Actually resend) a message in game chat
     */
    public static void sendMessage(Message message) {
        switch (message.getType()) {
            case chat -> sendGlobal(message);
            case join, leave -> sendConnection(message);
            case announce -> sendAnnounce(message);
            case team -> sendChatTeam(message);
            case direct -> sendPrivate(message);
        }
    }

    /**
     * Create and send an announcement
     */
    public static void sendNewAnnounce(String announce, Profile sender) {
        announce = ChatColor.translateAlternateColorCodes('&', announce.replace("\\n", System.lineSeparator()));
        try {
            RabbitMQ.rabbitSend(String.format("""
                {
                    "type": "announceEvent",
                    "event": "%s"
                    "message": "%s"
                }""", MainBungee.getEvent().getName(), ChatColor.stripColor(announce)));
        } catch (IOException | TimeoutException exception) {
            exception.printStackTrace();
        }
        MainBungee.log("Announce » " + ChatColor.stripColor(announce));
        StringBuilder prettyAnnounce = new StringBuilder();
        for (String splitLines : announce.split("\\r?\\n")) {
            for (String splitSize : splitLines.split("(?<=\\G.{37,}\\s)")) {
                if (splitSize.length() > 1) prettyAnnounce.append("   ")
                        .append(ChatColor.translateAlternateColorCodes('&', splitSize))
                        .append(System.lineSeparator());
            }
        }
        Message message;
        if (sender==null) message = new Message(prettyAnnounce.toString());
        else message = new Message(Message.Type.announce, prettyAnnounce.toString(), sender);
        sendAnnounce(message);
        MessageManager.save(message);
    }

    /**
     * Send announce in chat
     */
    public static void sendAnnounce(Message message) {
        ProxyServer.getInstance().getPlayers().forEach(player -> player.sendMessage(
                new TextComponent(MainBungee.getConfig().getString("proxy.messages.chat.announce")
                        .replaceAll("<ANNOUNCE>", message.getMessage()))));
    }

    /**
     * Create and send a private message
     */
    public static void sendNewPrivate(ProxiedPlayer sender, ProxiedPlayer receiver, String msg) {
        msg = ChatColor.translateAlternateColorCodes('&', msg);
        Message message = new Message(msg, ProfileManager.getProfile(sender), ProfileManager.getProfile(receiver));
        sendPrivate(message);
        MessageManager.save(message);
    }

    /**
     * Send private message to sender & receiver in chat
     */
    public static void sendPrivate(Message message) {
        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            if (player.getUniqueId().equals(message.getSender().getUuid())) {
                player.sendMessage(message.getSender().getUuid(), new TextComponent(
                        "§6[§cMoi §6> §c" + message.getReceiver().getUsername() + "§6]§r " + message.getMessage()));
            } else if (player.getUniqueId().equals(message.getReceiver().getUuid())) {
                player.sendMessage(message.getSender().getUuid(), new TextComponent(
                        "§6[§c" + message.getSender().getUsername() + " §6> §cMoi§6]§r " + message.getMessage()));
            } else if (player.hasPermission("mods.chat.private.see")) {
                player.sendMessage(message.getSender().getUuid(), new TextComponent("§6[§c" + message.getSender().getUsername()
                        + " §6> §c" + message.getReceiver().getUsername() + "§6]§r " + message.getMessage()));
            }
        }
    }

    /**
     * Create and send a team message
     */
    public static void sendNewChatTeam(Team team, ProxiedPlayer player, String msg) {
        Message message = new Message(msg, ProfileManager.getProfile(player), team);
        sendChatTeam(message);
        MessageManager.save(message);
    }

    /**
     * Send message to team in chat
     */
    public static void sendChatTeam(Message message) {
        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            if (message.getTeam().getMembersUUIDs().contains(player.getUniqueId())) {
                player.sendMessage(message.getSender().getUuid(), new TextComponent(
                        "§a[Team]§r " + message.getSender().getUsername() + " §b»§r " + message.getMessage()));
            } else if (player.hasPermission("mods.chat.team.see")) {
                player.sendMessage(message.getSender().getUuid(), new TextComponent("§a[" + message.getTeam().getTeamName()
                        + "]§r " + message.getSender().getUsername() + " §b»§r " + message));
            }
        }
    }

    /**
     * Create and send a connection notification message
     */
    public static void sendNewConnection(Message message) {
        try {
            RabbitMQ.rabbitSend(String.format("""
                        {
                            "type": "%s",
                            "event": "%s"
                            "message": "%s"
                        }""", message.getType().toString(), MainBungee.getEvent().getName(),
                    ChatColor.stripColor(message.getMessage())));
        } catch (IOException | TimeoutException exception) {
            exception.printStackTrace();
        }
        sendConnection(message);
        MessageManager.save(message);
    }

    /**
     * Send connection message to all players
     */
    public static void sendConnection(Message message) {
        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            player.sendMessage(new TextComponent(message.getMessage()));
        }
    }

    /**
     * Send message in chat
     */
    public static void sendNewGlobal(UUID uuid, String message) {
        Profile profile = ProfileManager.getProfile(uuid);
        String prefix = CoreUtils.getPrefix(uuid);
        String msg = ChatColor.translateAlternateColorCodes('&',
                prefix == null ? "" : prefix + " " + profile.getUsername() + " §b»§r " + message);

        Message message1 = new Message(msg, profile);

        if (!message1.isMuted()) {
            try {
                RabbitMQ.rabbitSend(String.format("""
                        {
                            "type": "chatEvent",
                            "event": "%s"
                            "message": "%s"
                        }""", MainBungee.getEvent().getName(), ChatColor.stripColor(msg)));
            } catch (IOException | TimeoutException exception) {
                exception.printStackTrace();
            }
        }
        // TODO: 24/09/2021 check here all things
        sendGlobal(message1);


        if (MuteManager.isMuted(profile)) {
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);
            if (player.isConnected()) {
                player.sendMessage(new TextComponent("§c§l[MUTE]§r " + msg));
                warnMute(player, MuteManager.getLastMute(profile));
            }
            ProxyServer.getInstance().getPlayers().stream()
                    .filter(proxiedPlayer -> proxiedPlayer.hasPermission("mods.mute.other.see"))
                    .forEach(mod-> mod.sendMessage(new TextComponent("§c§l[MUTE]§r " + msg)));
        } else {
            ProxyServer.getInstance().getPlayers().forEach(proxiedPlayer ->
                    proxiedPlayer.sendMessage(uuid, new TextComponent(msg)));

        }
        MainBungee.log(ChatColor.stripColor("<" + profile.getUsername() + "§r> " + message));
    }

    /**
     *
     */
    public static void sendGlobal(Message message) {

    }

    /**
     * Send time before unMute
     */
    public static void warnMute(ProxiedPlayer player, Mute mute) {
        TextComponent Mute = new TextComponent(CoreUtils.getString("proxy.chat.message.unMuteNotify.message")
                .replaceAll("<REAMING>", DateMileKat.reamingToString(mute.getPardonDate())));
        Mute.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(
                MainBungee.getConfig().getString("proxy.chat.message.unMuteNotify.hover"))));
        player.sendMessage(Mute);
    }

    /**
     * Mark a message as removed
     */
    public static void markRemove(Profile profile, String id) {
        MessageManager.save(MessageManager.getMessage(id).remove(profile, "§6Quick removed -> [X]"));
    }
}
