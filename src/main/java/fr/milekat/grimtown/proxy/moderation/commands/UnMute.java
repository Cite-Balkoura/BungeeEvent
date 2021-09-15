package fr.milekat.grimtown.proxy.moderation.commands;

import fr.milekat.grimtown.MainBungee;
import fr.milekat.grimtown.proxy.core.CoreUtils;
import fr.milekat.grimtown.proxy.core.classes.Profile;
import fr.milekat.grimtown.proxy.core.manager.ProfileManager;
import fr.milekat.grimtown.proxy.moderation.ModerationUtils;
import fr.milekat.grimtown.proxy.moderation.managers.MuteManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class UnMute extends Command {
    public UnMute() {
        super("unmute", "mods.command.unmute");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length >= 2) {
            Profile profile = ProfileManager.getProfile(args[0]);
            if (!MuteManager.isMuted(profile)) {
                sender.sendMessage(new TextComponent(MainBungee.PREFIX + "§cJoueur pas mute."));
                return;
            }
            ModerationUtils.unMuteSend(profile.getUuid(), ((ProxiedPlayer) sender).getUniqueId(), CoreUtils.getArgsText(1, args));
        } else {
            sendHelp(sender);
        }
    }

    /**
     * Help infos
     */
    private void sendHelp(CommandSender sender){
        sender.sendMessage(new TextComponent(MainBungee.PREFIX + "§6" + getClass().getSimpleName()));
        sender.sendMessage(new TextComponent("§6/unmute <player> <reason>:§r unmute le joueur."));
    }
}
