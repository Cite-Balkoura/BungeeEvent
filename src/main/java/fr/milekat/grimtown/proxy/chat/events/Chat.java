package fr.milekat.grimtown.proxy.chat.events;

import fr.milekat.grimtown.MainBungee;
import fr.milekat.grimtown.proxy.chat.ChatUtils;
import fr.milekat.grimtown.proxy.core.CoreUtils;
import fr.milekat.grimtown.proxy.core.classes.Profile;
import fr.milekat.grimtown.proxy.core.manager.ProfileManager;
import fr.milekat.grimtown.proxy.moderation.classes.Mute;
import fr.milekat.grimtown.proxy.moderation.managers.MuteManager;
import fr.milekat.grimtown.utils.RabbitMQ;
import fr.milekat.grimtown.utils.RabbitMQReceive;
import fr.milekat.utils.DateMileKat;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

public class Chat implements Listener {
    private final HashMap<UUID, String> MSG_LAST;
    private final HashMap<UUID, Integer> MSG_RECENT;
    private final ArrayList<ProxiedPlayer> CHAT_TEAM;

    public Chat(HashMap<UUID, String> msg_last, HashMap<UUID, Integer> msg_recent, ArrayList<ProxiedPlayer> chat_team) {
        this.MSG_LAST = msg_last;
        this.MSG_RECENT = msg_recent;
        this.CHAT_TEAM = chat_team;
    }

    @EventHandler
    public void onChat(ChatEvent event) {
        if (event.isCommand()) return;
        event.setCancelled(true);
        ProxiedPlayer player = (ProxiedPlayer) event.getSender();
        if (CHAT_TEAM.contains(player)) {
            ChatUtils.sendChatTeam(player, event.getMessage());
        } else {
            String message = cleanMessages(event.getMessage(), player.getUniqueId());
            if (message==null) return;
            sendChat(player.getUniqueId(), message);
        }
    }

    @EventHandler
    public void onDiscordChat(RabbitMQReceive event) {
        if (event.getType().equals(RabbitMQReceive.MessageType.chatEvent)) {
            UUID uuid = UUID.fromString((String) event.getPayload().get("uuid"));
            String message = cleanMessages((String) event.getPayload().get("message"), uuid);
            if (message != null) sendChat(uuid, message);
        } else if (event.getType().equals(RabbitMQReceive.MessageType.announceEvent)) {
            ChatUtils.sendAnnounce((String) event.getPayload().get("message"));
        }
    }

    /**
     * Send message in chat
     */
    private void sendChat(UUID uuid, String message) {
        Profile profile = ProfileManager.getProfile(uuid);
        String prefix = CoreUtils.getPrefix(uuid);
        prefix =  prefix == null ? "" : prefix + " ";
        String msg = ChatColor.translateAlternateColorCodes('&',
                prefix + profile.getUsername() + " §b»§r " + message);
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
        MainBungee.log(ChatColor.stripColor("<" + profile.getUsername() + "§r> " + message));
    }

    /**
     * Send time before unMute
     */
    private void warnMute(ProxiedPlayer player, Mute mute) {
        TextComponent Mute = new TextComponent(MainBungee.getConfig().getString("proxy.chat.message.unMuteNotify.message")
                .replaceAll("<PREFIX>", MainBungee.PREFIX)
                .replaceAll("<REAMING>", DateMileKat.reamingToString(mute.getPardonDate())));
        Mute.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(
                MainBungee.getConfig().getString("proxy.chat.message.unMuteNotify.hover"))));
        player.sendMessage(Mute);
    }

    /**
     * If safe_chat is enable, filter words, spamming and uppercase
     */
    private String cleanMessages(String message, UUID sender) {
        int msg_recent = MSG_RECENT.getOrDefault(sender,0) + 1;
        MSG_RECENT.put(sender, msg_recent);
        if (msg_recent > MainBungee.getConfig().getInt("proxy.chat.spam.limit.kick")) {
            if (isOnline(sender)) {
                ProxyServer.getInstance().getPlayer(sender)
                        .disconnect(new TextComponent(MainBungee.getConfig().getString("proxy.chat.spam.message.kick")
                                .replaceAll("<PREFIX>", MainBungee.PREFIX)));
            }
            return null;
        }
        if (msg_recent > MainBungee.getConfig().getInt("proxy.chat.spam.limit.warn")) {
            if (isOnline(sender)) {
                ProxyServer.getInstance().getPlayer(sender)
                        .sendMessage(new TextComponent(MainBungee.getConfig().getString("proxy.chat.spam.message.warn")
                                .replaceAll("<PREFIX>", MainBungee.PREFIX)));
            }
            return null;
        }
        if (MSG_LAST.getOrDefault(sender,"").equalsIgnoreCase(message)) {
            if (isOnline(sender)) {
                ProxyServer.getInstance().getPlayer(sender)
                        .sendMessage(new TextComponent(MainBungee.getConfig().getString("proxy.chat.spam.message.duplicate")
                                .replaceAll("<PREFIX>", MainBungee.PREFIX)));
            }
            return null;
        }
        MSG_LAST.put(sender, message);
        //  Maj remover
        if (message.length() > MainBungee.getConfig().getInt("proxy.chat.min_lower_case_length")) {
            int upperCase = 0;
            int lowerCase = 0;
            for (int k = 0; k < message.length(); k++) {
                if (Character.isUpperCase(message.charAt(k))) upperCase++;
                if (Character.isLowerCase(message.charAt(k))) lowerCase++;
            }
            if (upperCase>lowerCase) {
                message = message.toLowerCase();
            }
        }
        // Words black list
        String[] messages = message.split("\\s+");
        for (String word : messages) {
            if (MainBungee.getConfig().getList("proxy.chat.banned_words").contains(word.toLowerCase())) {
                message = message.replace(word, word.replaceAll(".","*"));
            }
        }
        return message;
    }

    /**
     * Quick check if player is online
     */
    private boolean isOnline(UUID uuid){
        return ProxyServer.getInstance().getPlayer(uuid) != null;
    }
}
