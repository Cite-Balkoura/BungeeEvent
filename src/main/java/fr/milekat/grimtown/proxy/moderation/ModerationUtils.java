package fr.milekat.grimtown.proxy.moderation;

import fr.milekat.grimtown.MainBungee;
import fr.milekat.grimtown.proxy.core.classes.Profile;
import fr.milekat.grimtown.proxy.core.manager.ProfileManager;
import fr.milekat.grimtown.utils.RabbitMQ;
import fr.milekat.utils.DateMileKat;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

public class ModerationUtils {
    /**
     * Check if command can be processed
     */
    public static boolean cantProcess(CommandSender sender, String[] args) {
        //  Check if target has a profile
        if (ProfileManager.notExists(args[0])) {
            sender.sendMessage(new TextComponent(MainBungee.PREFIX + "§cJoueur introuvable."));
            return true;
        }
        return false;
    }

    /**
     * Check if command can be processed (with delay arg)
     */
    public static boolean cantProcessDelay(CommandSender sender, String[] args) {
        if (cantProcess(sender, args)) return true;
        //  Check if delay less than 10s (10000ms)
        long time = DateMileKat.parsePeriod(args[1]) + new Date().getTime();
        //  Check if value is less than 10s (10000ms)
        if (time < (new Date().getTime() + 10000)) {
            sender.sendMessage(new TextComponent(MainBungee.PREFIX + "§cMerci d'indiquer un délais suppérieur à 10s."));
            return true;
        }
        return false;
    }

    /**
     * Process a mute of a player (Notify player)
     */
    public static void mute(UUID target, UUID sender, String reason) {
        Profile pTarget = ProfileManager.getProfile(target);
        Profile pSender = ProfileManager.getProfile(sender);
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(pTarget.getUuid());
        if (player!=null && player.isConnected()) {
            player.sendMessage(new TextComponent(MainBungee.PREFIX + "§cVous êtes mute !"));
            player.sendMessage(new TextComponent(MainBungee.PREFIX + "§6Raison:§r " + reason));
        }
        MainBungee.info(pTarget.getUsername() + " a été mute par " + pSender.getUsername() + " pour " + reason);
    }

    /**
     * Process a mute of a player (Notify player and send into Rabbit)
     */
    public static void muteSend(UUID target, UUID sender, Long delay, String reason) {
        mute(target, sender, reason);
        rabbitSend("mute", target.toString(), sender.toString(), String.valueOf(delay), reason);
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
        rabbitSend("unMute", target.toString(), sender.toString(), null, reason);
    }

    /**
     * Process a ban of a player
     */
    public static void ban(UUID target, UUID sender, Long delay, String reason) {
        Profile pTarget = ProfileManager.getProfile(target);
        Profile pSender = ProfileManager.getProfile(sender);
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(pTarget.getUuid());
        if (player!=null && player.isConnected()) {
            player.disconnect(new TextComponent(MainBungee.getConfig().getString("proxy.login.ban")
                    .replaceAll("<BAN_TIME>", DateMileKat.reamingToString(new Date(delay)))
                    .replaceAll("<REASON>", reason)));
        }
        MainBungee.info(pTarget.getUsername() + " a été ban par " + pSender.getUsername() + " pour " + reason);
    }

    /**
     * Process a ban of a player
     */
    public static void banSend(UUID target, UUID sender, Long delay, String reason) {
        ban(target, sender, delay, reason);
        rabbitSend("ban", target.toString(), sender.toString(), String.valueOf(delay), reason);
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
        rabbitSend("unBan", target.toString(), sender.toString(), null, reason);
    }

    /**
     * Send a  message through RabbitMq
     */
    public static void rabbitSend(String type, String target, String sender, String delay, String reason) {
        try {
            RabbitMQ.rabbitSend(String.format("{\"type\":\"%s\",\"target\":\"%s\",\"sender\":\"%s\",\"delay\":\"%s\"," +
                            "\"reason\": \"%s\"}", type, target, sender, delay, reason));
        } catch (IOException | TimeoutException exception) {
            MainBungee.warning(String.format("[Error] RabbitSend - %s", type));
            if (MainBungee.DEBUG_ERRORS) exception.printStackTrace();
        }
    }
}
