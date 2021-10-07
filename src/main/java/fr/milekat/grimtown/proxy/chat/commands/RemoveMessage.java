package fr.milekat.grimtown.proxy.chat.commands;

import fr.milekat.grimtown.proxy.chat.ChatUtils;
import fr.milekat.grimtown.proxy.core.manager.ProfileManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class RemoveMessage extends Command {
    public RemoveMessage() {
        super("remove-msg", "mods.chat.remove");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 1) {
            ChatUtils.markRemove(ProfileManager.getProfile((ProxiedPlayer) sender), args[0]);
            ProxyServer.getInstance().getPlayers().forEach(player -> player.sendMessage(
                    new TextComponent(new TextComponent(new String(new char[30]).replace("\0", "\n")))));
            ChatUtils.sendMessages(25, ProxyServer.getInstance().getPlayers());
        }
    }
}
