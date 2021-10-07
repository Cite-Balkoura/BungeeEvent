package fr.milekat.grimtown.proxy.chat.commands;

import fr.milekat.grimtown.MainBungee;
import fr.milekat.grimtown.event.features.classes.Team;
import fr.milekat.grimtown.event.features.manager.TeamManager;
import fr.milekat.grimtown.proxy.chat.ChatManager;
import fr.milekat.grimtown.proxy.chat.ChatUtils;
import fr.milekat.grimtown.proxy.core.CoreUtils;
import fr.milekat.grimtown.proxy.core.manager.ProfileManager;
import fr.milekat.grimtown.utils.McTools;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class ChatMode extends Command implements TabExecutor {
    private final HashMap<ProxiedPlayer, Team> CHAT_TEAM;
    public ChatMode(HashMap<ProxiedPlayer, Team> chat_team) {
        super("chat");
        this.CHAT_TEAM = chat_team;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) return;
        MainBungee.log(args.length + "");
        if (args.length==1 && args[0].equalsIgnoreCase("all")) {
            CHAT_TEAM.remove(((ProxiedPlayer) sender));
            sender.sendMessage(new TextComponent(CoreUtils.getString("proxy.chat.messages.mode.all")));
        } else if (args.length==2 && args[0].equalsIgnoreCase("mods") && sender.hasPermission("mods.chat.mods")) {
            if (args[1].equalsIgnoreCase("on")) {
                ChatManager.getMods().add((ProxiedPlayer) sender);
                sender.sendMessage(new TextComponent("Mods enable"));
            } else {
                ChatManager.getMods().remove((ProxiedPlayer) sender);
                sender.sendMessage(new TextComponent("Mods disable"));
            }
            sender.sendMessage(new TextComponent(new TextComponent(new String(new char[15]).replace("\0", "\n"))));
            ChatUtils.sendMessages(15, Collections.singleton((ProxiedPlayer) sender));
        } else if (args.length>=1 && args[0].equalsIgnoreCase("team")) {
            Team team = null;
            if (args.length==1) {
                team = TeamManager.getTeam(((ProxiedPlayer) sender).getUniqueId());
            } else if (sender.hasPermission("mods.chat.team.other")) {
                team = TeamManager.getTeam(ProfileManager.getProfile(args[1]));
            }
            if (team == null) {
                sender.sendMessage(new TextComponent(CoreUtils.getString("proxy.chat.messages.mode.team")));
            } else {
                CHAT_TEAM.put(((ProxiedPlayer) sender), team);
                sender.sendMessage(new TextComponent(CoreUtils.getString("proxy.core.messages.no_team")));
            }
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
        if (sender.hasPermission("mods.chat.mods")) sender.sendMessage(new TextComponent("§6/chat mods <on/off>"));
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length <= 1) {
            if (sender.hasPermission("mods.chat.mods")) {
                return McTools.getTabArgs(args[0], new ArrayList<>(Arrays.asList("all", "team", "mods")));
            } else return McTools.getTabArgs(args[0], new ArrayList<>(Arrays.asList("all", "team")));
        } else if (args.length <= 2 && args[0].equalsIgnoreCase("mods")) {
            return McTools.getTabArgs(args[1], new ArrayList<>(Arrays.asList("on", "off")));
        }
        return new ArrayList<>();
    }
}
