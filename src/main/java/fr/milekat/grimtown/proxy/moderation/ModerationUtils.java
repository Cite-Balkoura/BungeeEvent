package fr.milekat.grimtown.proxy.moderation;

import fr.milekat.grimtown.MainBungee;
import fr.milekat.grimtown.proxy.core.classes.Profile;
import fr.milekat.grimtown.proxy.core.manager.ProfileManager;
import fr.milekat.grimtown.utils.RabbitMQ;
import fr.milekat.utils.DateMileKat;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

public class ModerationUtils {
    /**
     * Process a mute of a player (Notify player)
     */
    public static void mute(UUID target, UUID sender, String reason) {
        Profile pTarget = ProfileManager.getProfile(target);
        Profile pMod = ProfileManager.getProfile(sender);
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(pTarget.getUuid());
        if (player!=null && player.isConnected()) {
            player.sendMessage(new TextComponent(MainBungee.PREFIX + "§cVous êtes mute !"));
            player.sendMessage(new TextComponent(MainBungee.PREFIX + "§6Raison:§r " + reason));
        }
        MainBungee.info(pTarget.getUsername() + " a été mute par " + pMod.getUsername() + " pour " + reason);
    }

    /**
     * Process a mute of a player (Notify player and send into Rabbit)
     */
    public static void muteSend(UUID target, UUID sender, Long time, String reason) {
        mute(target, sender, reason);
        try {
            RabbitMQ.rabbitSend(String.format("""
                        {
                            "type": "ban",
                            "target": "%s",
                            "sender": "%s",
                            "delay": "%s",
                            "reason": "%s"
                        }""", target, sender, time, reason));
        } catch (IOException | TimeoutException exception) {
            MainBungee.warning("[Error] RabbitSend - mute");
            if (MainBungee.DEBUG_ERRORS) exception.printStackTrace();
        }
    }

    /**
     * Process an unMute of a player
     */
    public static void unMute(UUID target) {
        Profile pTarget = ProfileManager.getProfile(target);
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(pTarget.getUuid());
        if (player!=null && player.isConnected()) {
            player.sendMessage(new TextComponent(MainBungee.PREFIX + "§2Vous n'êtes plus mute !"));
            player.sendMessage(new TextComponent(MainBungee.PREFIX + "§6Soyez plus vigilant à l'avenir !"));
        }
        MainBungee.info(pTarget.getUsername() + " n'est plus mute !");
    }

    /**
     * Process an unMute of a player
     */
    public static void unMuteSend(UUID target, UUID sender, String reason) {
        unMute(target);
        try {
            RabbitMQ.rabbitSend(String.format("""
                        {
                            "type": "unmute",
                            "target": "%s",
                            "sender": "%s",
                            "reason": "%s"
                        }""", target, sender, reason));
        } catch (IOException | TimeoutException exception) {
            MainBungee.warning("[Error] RabbitSend - unmute");
            if (MainBungee.DEBUG_ERRORS) exception.printStackTrace();
        }
    }

    /**
     * Process a ban of a player
     */
    public static void ban(UUID target, UUID sender, Long time, String reason) {
        Profile pTarget = ProfileManager.getProfile(target);
        Profile pMod = ProfileManager.getProfile(sender);
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(pTarget.getUuid());
        if (player!=null && player.isConnected()) {
            player.disconnect(new TextComponent(MainBungee.getConfig().getString("connection.ban")
                    .replaceAll("@time", DateMileKat.reamingToString(new Date(time)))
                    .replaceAll("@reason", reason)));
        }
        MainBungee.info(pTarget.getUsername() + " a été ban par " + pMod.getUsername() + " pour " + reason);
    }

    /**
     * Process a ban of a player
     */
    public static void banSend(UUID target, UUID sender, Long time, String reason) {
        ban(target, sender, time, reason);
        try {
            RabbitMQ.rabbitSend(String.format("""
                        {
                            "type": "ban",
                            "target": "%s",
                            "sender": "%s",
                            "delay": "%s",
                            "reason": "%s"
                        }""", target, sender, time, reason));
        } catch (IOException | TimeoutException exception) {
            MainBungee.warning("[Error] RabbitSend - ban");
            if (MainBungee.DEBUG_ERRORS) exception.printStackTrace();
        }
    }

    /**
     * Process an unBan of a player
     */
    public static void unBan(UUID target) {
        Profile pTarget = ProfileManager.getProfile(target);
        MainBungee.info(pTarget.getUsername() + " n'est plus ban !");
    }

    /**
     * Process an unBan of a player
     */
    public static void unBanSend(UUID target, UUID sender, String reason) {
        unBan(target);
        try {
            RabbitMQ.rabbitSend(String.format("""
                        {
                            "type": "unban",
                            "target": "%s",
                            "sender": "%s",
                            "reason": "%s"
                        }""", target, sender, reason));
        } catch (IOException | TimeoutException exception) {
            MainBungee.warning("[Error] RabbitSend - unban");
            if (MainBungee.DEBUG_ERRORS) exception.printStackTrace();
        }
    }
}
