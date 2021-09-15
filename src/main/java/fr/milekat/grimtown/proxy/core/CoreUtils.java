package fr.milekat.grimtown.proxy.core;

import fr.milekat.utils.Tools;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class CoreUtils {
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
}
