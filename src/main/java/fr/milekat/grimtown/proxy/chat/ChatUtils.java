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
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

public class ChatUtils {
    public static void sendMessages(int count, Collection<ProxiedPlayer> receivers) {
        MessageManager.getLast(count).forEach(message -> sendMessage(message, receivers));
    }

    /**
     * Send (Actually resend) a message in game chat
     */
    public static void sendMessage(Message message, Collection<ProxiedPlayer> receivers) {
        switch (message.getType()) {
            case chat -> sendGlobal(message, receivers);
            case join, leave -> sendConnection(message, receivers);
            case announce -> sendAnnounce(message, receivers);
            case team -> sendChatTeam(message, receivers);
            case direct -> sendPrivate(message, receivers);
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
                    "event": "%s",
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
        sendAnnounce(message, ProxyServer.getInstance().getPlayers());
        ProxyServer.getInstance().getScheduler().runAsync(MainBungee.getInstance(), ()-> MessageManager.save(message));
    }

    /**
     * Send announce in chat
     */
    public static void sendAnnounce(Message message, Collection<ProxiedPlayer> receivers) {
        receivers.forEach(player ->
                player.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&',
                        MainBungee.getConfig().getString("proxy.chat.messages.announces")
                                .replaceAll("<ANNOUNCE>", message.getMessage()))))
        );
    }

    /**
     * Create and send a private message
     */
    public static void sendNewPrivate(ProxiedPlayer sender, ProxiedPlayer receiver, String msg) {
        msg = ChatColor.translateAlternateColorCodes('&', msg);
        Message message = new Message(msg, ProfileManager.getProfile(sender), ProfileManager.getProfile(receiver));
        sendPrivate(message, ProxyServer.getInstance().getPlayers());
        ProxyServer.getInstance().getScheduler().runAsync(MainBungee.getInstance(), ()-> MessageManager.save(message));
    }

    /**
     * Send private message to sender & receiver in chat
     */
    public static void sendPrivate(Message message, Collection<ProxiedPlayer> receivers) {
        receivers.forEach(player -> {
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
        });
    }

    /**
     * Create and send a team message
     */
    public static void sendNewChatTeam(Team team, ProxiedPlayer player, String msg) {
        Message message = new Message(msg, ProfileManager.getProfile(player), team);
        sendChatTeam(message, ProxyServer.getInstance().getPlayers());
        ProxyServer.getInstance().getScheduler().runAsync(MainBungee.getInstance(), ()-> MessageManager.save(message));
    }

    /**
     * Send message to team in chat
     */
    public static void sendChatTeam(Message message, Collection<ProxiedPlayer> receivers) {
        receivers.forEach(player -> {
            if (message.getTeam().getMembers().contains(player.getUniqueId())) {
                player.sendMessage(message.getSender().getUuid(), new TextComponent(
                        "§a[Team]§r " + message.getSender().getUsername() + " §b»§r " + message.getMessage()));
            } else if (player.hasPermission("mods.chat.team.see")) {
                player.sendMessage(message.getSender().getUuid(), new TextComponent("§a[" + message.getTeam().getTeamName()
                        + "]§r " + message.getSender().getUsername() + " §b»§r " + message));
            }
        });
    }

    /**
     * Create and send a connection notification message
     */
    public static void sendNewConnection(Message message) {
        Collection<ProxiedPlayer> receivers = new ArrayList<>(ProxyServer.getInstance().getPlayers());
        receivers.remove(ProxyServer.getInstance().getPlayer(message.getSender().getUuid()));
        sendConnection(message, receivers);
        ProxyServer.getInstance().getScheduler().runAsync(MainBungee.getInstance(), ()-> {
            try {
                RabbitMQ.rabbitSend(String.format("""
                        {
                            "type": "%s",
                            "event": "%s",
                            "message": "%s"
                        }""", message.getType().toString(), MainBungee.getEvent().getName(),
                        ChatColor.stripColor(message.getMessage())));
            } catch (IOException | TimeoutException exception) {
                exception.printStackTrace();
            }
            MessageManager.save(message);
            MainBungee.log(ChatColor.stripColor(message.getMessage()));
        });
    }

    /**
     * Send connection message to all players
     */
    public static void sendConnection(Message message, Collection<ProxiedPlayer> receivers) {
        receivers.forEach(player -> player.sendMessage(new TextComponent(message.getMessage())));
    }

    /**
     * Create and send a new global message
     */
    public static void sendNewGlobal(UUID uuid, String strMessage) {
        Profile profile = ProfileManager.getProfile(uuid);
        Message message = new Message(strMessage, profile);
        sendGlobal(message, ProxyServer.getInstance().getPlayers());
        ProxyServer.getInstance().getScheduler().runAsync(MainBungee.getInstance(), ()-> {
            if (message.isMuted()) {
                warnMute(ProxyServer.getInstance().getPlayer(uuid), MuteManager.getLastMute(message.getSender()));
            } else {
                try {
                    RabbitMQ.rabbitSend(String.format("""
                            {
                                "type": "chatEvent",
                                "event": "%s",
                                "sender": "%s",
                                "message": "%s"
                            }""", MainBungee.getEvent().getName(), profile.getUsername(), ChatColor.stripColor(strMessage)));
                } catch (IOException | TimeoutException exception) {
                    exception.printStackTrace();
                }
            }
            MessageManager.save(message);
            MainBungee.log(ChatColor.stripColor("§r<" + profile.getUsername() + "§r> " + strMessage));
        });
    }

    /**
     * Send global message to all players
     */
    public static void sendGlobal(Message message, Collection<ProxiedPlayer> receivers) {
        String prefix = CoreUtils.getPrefix(message.getSender().getUuid());
        String msg = ChatColor.translateAlternateColorCodes('&', (prefix == null ? "" : prefix + " ") +
                message.getSender().getUsername() + " §b»§r " + message.getMessage());
        TextComponent modsMsg = messageModsBuilder(message, msg);
        if (message.isMuted()) {
            TextComponent muteMsg = new TextComponent("§c[MUTE]§r ");
            muteMsg.addExtra(modsMsg);
            receivers.forEach(player -> {
                if (ChatManager.getMods().contains(player)) {
                    player.sendMessage(muteMsg);
                } else if (player.getUniqueId().equals(message.getSender().getUuid())) {
                    player.sendMessage(new TextComponent("§c[MUTE]§r " + msg));
                }
            });
        } else {
            receivers.forEach(player -> {
                if (ChatManager.getMods().contains(player)) {
                    player.sendMessage(modsMsg);
                } else {
                    player.sendMessage(new TextComponent(msg));
                }
            });
        }
    }

    /**
     * Get a message for mods
     */
    private static TextComponent messageModsBuilder(Message message, String msg){
        TextComponent Chat = new TextComponent();
        if (message.isRemoved()){
            TextComponent removedMsg = new TextComponent("§c<Message from " + message.getSender().getUsername() +
                    " removed by " + message.getRemove().getRemover().getUsername() + ">");
            removedMsg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(msg)));
            Chat.addExtra(removedMsg);
        } else {
            Chat.addExtra(new TextComponent(msg));
            TextComponent DelButton = new TextComponent("§r§c [X]");
            DelButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§cRemove this message ?")));
            DelButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/remove-msg " + message.getId()));
            Chat.addExtra(DelButton);
            if (!message.isMuted()) {
                TextComponent MuteButton = new TextComponent("§r§c [MUTE]");
                MuteButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§cMute 5 minutes")));
                MuteButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                        "/mute " + message.getSender().getUsername() + " 5m fast mute"));
                Chat.addExtra(MuteButton);
            } else {
                TextComponent unMuteButton = new TextComponent("§r§c [UNMUTE]");
                unMuteButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§cUnmute player")));
                unMuteButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                        "/unmute " + message.getSender().getUsername() + " fast unmute"));
                Chat.addExtra(unMuteButton);
            }
        }
        return Chat;
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
        MessageManager.updateRemove(MessageManager.getMessage(id).remove(profile, "§6Quick removed -> [X]"));
    }
}
