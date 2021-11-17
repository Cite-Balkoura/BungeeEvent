package fr.milekat.grimtown.proxy.chat.events;

import fr.milekat.grimtown.MainBungee;
import fr.milekat.grimtown.proxy.chat.ChatUtils;
import fr.milekat.grimtown.proxy.chat.classes.Team;
import fr.milekat.grimtown.proxy.chat.manager.TeamManager;
import fr.milekat.grimtown.proxy.core.CoreUtils;
import fr.milekat.grimtown.utils.RabbitMQReceive;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.bson.types.ObjectId;

import java.util.HashMap;
import java.util.UUID;

public class Chat implements Listener {
    private final HashMap<UUID, String> MSG_LAST;
    private final HashMap<UUID, Integer> MSG_RECENT;
    private final HashMap<ProxiedPlayer, Team> CHAT_TEAM;

    public Chat(HashMap<UUID, String> msg_last, HashMap<UUID, Integer> msg_recent, HashMap<ProxiedPlayer, Team> chat_team) {
        this.MSG_LAST = msg_last;
        this.MSG_RECENT = msg_recent;
        this.CHAT_TEAM = chat_team;
    }

    @EventHandler
    public void onChat(ChatEvent event) {
        if (event.isCommand()) return;
        event.setCancelled(true);
        ProxiedPlayer player = (ProxiedPlayer) event.getSender();
        if (CHAT_TEAM.containsKey(player)) {
            ChatUtils.sendNewChatTeam(CHAT_TEAM.get(player), player.getUniqueId(), event.getMessage());
        } else {
            String message = cleanMessages(event.getMessage(), player.getUniqueId());
            if (message != null) ChatUtils.sendNewGlobal(player.getUniqueId(), message);
        }
    }

    @EventHandler
    public void onDiscordChat(RabbitMQReceive event) {
        if (!MainBungee.getEvent().isRunning()) return;
        if (event.getType().equals(RabbitMQReceive.MessageType.chatGlobal)) {
            UUID uuid = UUID.fromString((String) event.getPayload().get("uuid"));
            String message = cleanMessages((String) event.getPayload().get("message"), uuid);
            if (message != null) ChatUtils.sendNewGlobal(uuid, message);
            else MainBungee.warning("Wrong chatGlobal json: " + event.getPayload());
        } else if (event.getType().equals(RabbitMQReceive.MessageType.chatTeam)) {
            UUID uuid = UUID.fromString((String) event.getPayload().get("uuid"));
            Team team = TeamManager.getTeam(new ObjectId((String) event.getPayload().get("teamId")));
            String message = (String) event.getPayload().get("message");
            if (team!=null && message!=null) ChatUtils.sendNewChatTeam(team, uuid, message);
            else MainBungee.warning("Wrong chatTeam json: " + event.getPayload());
        }
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
                        .disconnect(new TextComponent(CoreUtils.getString("proxy.chat.spam.message.kick")));
            }
            return null;
        }
        if (msg_recent > MainBungee.getConfig().getInt("proxy.chat.spam.limit.warn")) {
            if (isOnline(sender)) {
                ProxyServer.getInstance().getPlayer(sender)
                        .sendMessage(new TextComponent(CoreUtils.getString("proxy.chat.spam.message.warn")));
            }
            return null;
        }
        if (MSG_LAST.getOrDefault(sender,"").equalsIgnoreCase(message)) {
            if (isOnline(sender)) {
                ProxyServer.getInstance().getPlayer(sender)
                        .sendMessage(new TextComponent(CoreUtils.getString("proxy.chat.spam.message.duplicate")));
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
                message = message.replace(word, word.replaceAll(".*","*"));
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
