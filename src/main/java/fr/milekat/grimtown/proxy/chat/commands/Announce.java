package fr.milekat.grimtown.proxy.chat.commands;

import fr.milekat.grimtown.MainBungee;
import fr.milekat.grimtown.proxy.chat.ChatUtils;
import fr.milekat.grimtown.proxy.core.CoreUtils;
import fr.milekat.grimtown.proxy.core.manager.ProfileManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class Announce extends Command {
    public Announce() {
        super("broadcast", "mods.chat.announce", "bc", "alert", "say");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sendHelp(sender);
        } else {
            ChatUtils.sendNewAnnounce(CoreUtils.getArgsText(0, args),
                    ProfileManager.getProfile(((ProxiedPlayer) sender).getUniqueId()));
        }
    }

    /**
     * Help infos
     */
    private void sendHelp(CommandSender sender){
        sender.sendMessage(new TextComponent(MainBungee.PREFIX + "§6" + getClass().getSimpleName()));
        sender.sendMessage(new TextComponent("§6/bc <message>:§r envoyer une annonce dans le chat."));
    }
}
