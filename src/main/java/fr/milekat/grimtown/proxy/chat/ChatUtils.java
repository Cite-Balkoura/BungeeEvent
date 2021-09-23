package fr.milekat.grimtown.proxy.chat;

import fr.milekat.grimtown.MainBungee;
import fr.milekat.grimtown.event.features.classes.Team;
import fr.milekat.grimtown.event.features.manager.TeamManager;
import fr.milekat.grimtown.proxy.core.classes.Profile;
import fr.milekat.grimtown.utils.RabbitMQ;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ChatUtils {
    /**
     * Process an announcement (Bungee Chat + Discord)
     */
    public static void sendAnnounce(String announce) {
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
        ProxyServer.getInstance().getPlayers().forEach(player -> player.sendMessage(
                new TextComponent(MainBungee.getConfig().getString("proxy.messages.chat.announce")
                .replaceAll("<ANNOUNCE>", prettyAnnounce.toString()))));
    }

    /**
     * Send a private message
     */
    public static void sendPrivate(ProxiedPlayer sender, ProxiedPlayer receiver, String message) {
        String msg = ChatColor.translateAlternateColorCodes('&', message);
        sender.sendMessage(sender.getUniqueId(), new TextComponent("§6[§cMoi §6> §c" + receiver.getName() + "§6]§r " + msg));
        receiver.sendMessage(sender.getUniqueId(), new TextComponent("§6[§c" + sender.getName() + " §6> §cMoi§6]§r " + msg));
        ProxyServer.getInstance().getPlayers().stream()
                .filter(proxiedPlayer -> proxiedPlayer.hasPermission("mods.chat.see.private"))
                .forEach(loop -> loop.sendMessage(sender.getUniqueId(),
                        new TextComponent("§6[§c" + sender.getName() + " §6> §c" + receiver.getName() + "§6]§r " + msg)));
    }

    /**
     * Send message in chat for team
     */
    public static void sendChatTeam(ProxiedPlayer player, String message) {
        Team team = TeamManager.getTeam(player.getUniqueId());
        if (team==null) {
            // TODO: 23/09/2021 msg
            player.sendMessage(new TextComponent("No team found."));
            return;
        }
        for (Profile member : team.getMembers()) {
            ProxiedPlayer pMember = ProxyServer.getInstance().getPlayer(member.getUuid());
            if (pMember==null || !pMember.isConnected()) continue;
            pMember.sendMessage(player.getUniqueId(), new TextComponent("§a[Team]§r " + player.getName() + " §b»§r " + message));
        }
        ProxyServer.getInstance().getPlayers().stream().filter(proxiedPlayer -> proxiedPlayer.hasPermission("mods.chat.see.team"))
                .forEach(loop -> loop.sendMessage(player.getUniqueId(),
                        new TextComponent("§a[" + team.getTeamName() + "]§r " + player.getName() + " §b»§r " + message)));
    }
}
