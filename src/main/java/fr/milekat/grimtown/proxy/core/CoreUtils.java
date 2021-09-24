package fr.milekat.grimtown.proxy.core;

import fr.milekat.grimtown.MainBungee;
import fr.milekat.grimtown.event.classes.Event;
import fr.milekat.grimtown.proxy.moderation.classes.Ban;
import fr.milekat.grimtown.proxy.moderation.classes.Mute;
import fr.milekat.utils.DateMileKat;
import fr.milekat.utils.Tools;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.md_5.bungee.api.ChatColor;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class CoreUtils {
    /**
     * Get LuckPerms player prefix
     */
    public static String getPrefix(UUID uuid) {
        LuckPerms luckPerms = LuckPermsProvider.get();
        CompletableFuture<User> userLoadTask = luckPerms.getUserManager().loadUser(uuid);
        return userLoadTask.join().getCachedData().getMetaData().getPrefix();
    }

    /**
     * Concatenates args from minecraft command
     */
    public static String getArgsText(int skip_args, String... args) {
        StringBuilder sb = new StringBuilder();
        for (int loop=0; loop < args.length; loop++) {
            if (loop < skip_args) continue;
            sb.append(args[loop]).append(" ");
        }
        return Tools.remLastChar(sb.toString());
    }

    /**
     * Get a config string message, apply minecraft color and then replace all event placeholders
     */
    public static String getString(String path) {
        Event event = MainBungee.getEvent()==null ? new Event() : MainBungee.getEvent();
        return ChatColor.translateAlternateColorCodes('&', MainBungee.getConfig().getString(path))
                .replaceAll("<PREFIX>", MainBungee.PREFIX)
                .replaceAll("<EVENT_START_TIME>", DateMileKat.reamingToString(event.getStartDate()))
                .replaceAll("<MAINTENANCE_TIME>", DateMileKat.reamingToString(event.getMaintenanceDate()));
    }

    /**
     * Get a config string message and apply ban things
     */
    public static String getString(String path, Ban ban) {
        return getString(path).replaceAll("<BAN_TIME>", DateMileKat.reamingToString(ban.getPardonDate()))
                .replaceAll("<REASON>", ban.getReasonBan());
    }

    /**
     * Get a config string message and apply mute things
     */
    public static String getString(String path, Mute mute) {
        return getString(path).replaceAll("<MUTE_TIME>", DateMileKat.reamingToString(mute.getPardonDate()))
                .replaceAll("<REASON>", mute.getReasonMute());
    }
}
