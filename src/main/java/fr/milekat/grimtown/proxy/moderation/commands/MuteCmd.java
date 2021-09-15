package fr.milekat.grimtown.proxy.moderation.commands;

import fr.milekat.grimtown.MainBungee;
import fr.milekat.grimtown.proxy.core.CoreUtils;
import fr.milekat.grimtown.proxy.core.classes.Profile;
import fr.milekat.grimtown.proxy.core.manager.ProfileManager;
import fr.milekat.grimtown.proxy.moderation.ModerationUtils;
import fr.milekat.grimtown.utils.McTools;
import fr.milekat.utils.DateMileKat;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;

public class MuteCmd extends Command implements TabExecutor {
    public MuteCmd() {
        super("mute", "mods.command.mute");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length >= 3) {
            if (!ProfileManager.exists(args[0])) {
                sender.sendMessage(new TextComponent(MainBungee.PREFIX + "§cJoueur introuvable."));
                return;
            }
            Profile profile = ProfileManager.getProfile(args[0]);
            long time = DateMileKat.parsePeriod(args[1]) + new Date().getTime();
            //  Check if value is less than 10s (10000ms)
            if (time < (new Date().getTime() + 10000)) {
                sender.sendMessage(new TextComponent(MainBungee.PREFIX + "§cMerci d'indiquer un délais suppérieur à 10s."));
                return;
            }
            ModerationUtils.muteSend(profile.getUuid(), ((ProxiedPlayer) sender).getUniqueId(), time, CoreUtils.getArgsText(2, args));
        } else {
            sendHelp(sender);
        }
    }

    /**
     * Help infos
     */
    private void sendHelp(CommandSender sender){
        sender.sendMessage(new TextComponent(MainBungee.PREFIX + "§6" + getClass().getSimpleName()));
        sender.sendMessage(new TextComponent("§6/mute <player> <time> <reason>:§r Mute le joueur."));
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length <= 1) {
            return McTools.getTabArgs(args[0], ProxyServer.getInstance().getPlayers().stream().map(ProxiedPlayer::getName).collect(Collectors.toList()));
        }
        return new ArrayList<>();
    }
}
