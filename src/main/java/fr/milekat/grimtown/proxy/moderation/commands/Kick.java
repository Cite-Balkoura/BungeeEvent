package fr.milekat.grimtown.proxy.moderation.commands;

import fr.milekat.grimtown.MainBungee;
import fr.milekat.grimtown.proxy.core.CoreUtils;
import fr.milekat.grimtown.proxy.moderation.ModerationUtils;
import fr.milekat.grimtown.utils.McTools;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class Kick extends Command implements TabExecutor {
    public Kick() {
        super("kick", "mods.command.kick", "eject");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(new TextComponent("Not working from console"));
            return;
        }
        if (args.length > 0) {
            ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);
            if (target==null || !target.isConnected()) {
                sender.sendMessage(new TextComponent(MainBungee.PREFIX + "§cJoueur introuvable."));
                return;
            }
            target.disconnect(new TextComponent(MainBungee.getConfig().getString("connection.kick")
                    .replaceAll("@reason", CoreUtils.getArgsText(1, args))));
            ModerationUtils.rabbitSend("kick", target.getUniqueId().toString(),
                    ((ProxiedPlayer) sender).getUniqueId().toString(), null, CoreUtils.getArgsText(1, args));
        } else {
            sendHelp(sender);
        }
    }

    /**
     * Help infos
     */
    private void sendHelp(CommandSender sender){
        sender.sendMessage(new TextComponent(MainBungee.PREFIX + "§6" + getClass().getSimpleName()));
        sender.sendMessage(new TextComponent("§6/kick <player>:§r Kick le joueur."));
        sender.sendMessage(new TextComponent("§6/kick <player> <reason>:§r Kick le joueur avec un motif."));
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length <= 1) {
            return McTools.getTabArgs(args[0], ProxyServer.getInstance().getPlayers().stream().map(ProxiedPlayer::getName).collect(Collectors.toList()));
        }
        return new ArrayList<>();
    }
}
