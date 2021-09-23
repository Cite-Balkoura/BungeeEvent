package fr.milekat.grimtown.proxy.chat.commands;

import fr.milekat.grimtown.MainBungee;
import fr.milekat.grimtown.proxy.chat.ChatUtils;
import fr.milekat.grimtown.proxy.core.CoreUtils;
import fr.milekat.grimtown.proxy.core.classes.Profile;
import fr.milekat.grimtown.proxy.core.manager.ProfileManager;
import fr.milekat.grimtown.proxy.moderation.managers.MuteManager;
import fr.milekat.grimtown.utils.McTools;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

public class PrivateMessage extends Command implements TabExecutor {
    private final HashMap<ProxiedPlayer, ProxiedPlayer> PRIVATE_LAST;
    public PrivateMessage(HashMap<ProxiedPlayer, ProxiedPlayer> private_last) {
        super("m", "", "mp", "dm", "msg", "message", "private", "tell", "w", "whisper");
        this.PRIVATE_LAST = private_last;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Profile profile = ProfileManager.getProfile(((ProxiedPlayer) sender).getUniqueId());
        if (MuteManager.isMuted(profile)) {
            // TODO: 23/09/2021 msg
            sender.sendMessage(new TextComponent(MainBungee.PREFIX + "§cTu es mute !"));
            return;
        }
        if (args.length < 2) {
            sendHelp(sender);
            return;
        }
        if (sender.getName().equalsIgnoreCase(args[0])) {
            // TODO: 23/09/2021 msg
            sender.sendMessage(new TextComponent(MainBungee.PREFIX + "§cTu ne peux t'envoyer de MP !"));
            return;
        }
        ProxiedPlayer pSender = ProxyServer.getInstance().getPlayer(sender.getName());
        ProxiedPlayer pDest = ProxyServer.getInstance().getPlayer(args[0]);
        if (pDest==null || !pDest.isConnected()) {
            // TODO: 23/09/2021 msg
            sender.sendMessage(new TextComponent(MainBungee.PREFIX + "§cLe joueur est introuvable."));
            return;
        }
        ChatUtils.sendPrivate(pSender, pDest, CoreUtils.getArgsText(1, args));
        PRIVATE_LAST.put(pSender, pDest);
        PRIVATE_LAST.put(pDest, pSender);
    }

    /**
     * Help infos
     */
    private void sendHelp(CommandSender sender){
        sender.sendMessage(new TextComponent(MainBungee.PREFIX + "§6" + getClass().getSimpleName()));
        sender.sendMessage(new TextComponent("§6/mp <Destinataire> <Message>:§r envoyer message privé au destinataire."));
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length <= 1) {
            return McTools.getTabArgs(args[0], ProxyServer.getInstance().getPlayers().stream()
                    .map(ProxiedPlayer::getName).collect(Collectors.toList()));
        }
        return new ArrayList<>();
    }
}
