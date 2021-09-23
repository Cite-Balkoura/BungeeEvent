package fr.milekat.grimtown.proxy.chat.commands;

import fr.milekat.grimtown.MainBungee;
import fr.milekat.grimtown.proxy.chat.ChatUtils;
import fr.milekat.grimtown.proxy.core.CoreUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class ChatTeam extends Command {
    public ChatTeam() {
        super("teamchat", "", "tc", "tchat");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) return;
        if (args.length > 0) {
            ChatUtils.sendChatTeam((ProxiedPlayer) sender, CoreUtils.getArgsText(0, args));
        } else sendHelp(sender);
    }

    /**
     * Help infos
     */
    private void sendHelp(CommandSender sender){
        sender.sendMessage(new TextComponent(MainBungee.PREFIX + "§6" + getClass().getSimpleName()));
        sender.sendMessage(new TextComponent("§6/teamchat <message>:§r Envoi un message à votre équipe."));
    }
}
