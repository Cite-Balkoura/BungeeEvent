package fr.milekat.grimtown.proxy.chat.commands;

import fr.milekat.grimtown.MainBungee;
import fr.milekat.grimtown.utils.McTools;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;
import java.util.Arrays;

public class ChatMode extends Command implements TabExecutor {
    private final ArrayList<ProxiedPlayer> CHAT_TEAM;
    public ChatMode(ArrayList<ProxiedPlayer> chat_team) {
        super("chat");
        this.CHAT_TEAM = chat_team;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) return;
        if (args.length==1 && args[0].equalsIgnoreCase("all")) {
            CHAT_TEAM.remove(((ProxiedPlayer) sender));
            // TODO: 23/09/2021 Msg
            sender.sendMessage(new TextComponent(MainBungee.PREFIX + "§6Chat général activé"));
        } else if (args.length==1 && args[0].equalsIgnoreCase("team")) {
            CHAT_TEAM.add(((ProxiedPlayer) sender));
            // TODO: 23/09/2021 Msg
            sender.sendMessage(new TextComponent(MainBungee.PREFIX + "§6Chat équipe activé"));
        } else {
            sendHelp(sender);
        }
    }

    /**
     * Help infos
     */
    private void sendHelp(CommandSender sender){
        sender.sendMessage(new TextComponent(MainBungee.PREFIX + "§6" + getClass().getSimpleName()));
        sender.sendMessage(new TextComponent("§6/chat all:§r Passe votre chat en mode général."));
        sender.sendMessage(new TextComponent("§6/chat team:§r Passe votre chat en mode équipe."));
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length <= 1) {
            return McTools.getTabArgs(args[0], new ArrayList<>(Arrays.asList("all", "team")));
        }
        return new ArrayList<>();
    }
}
