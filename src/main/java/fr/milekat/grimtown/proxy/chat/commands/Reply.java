package fr.milekat.grimtown.proxy.chat.commands;

import fr.milekat.grimtown.MainBungee;
import fr.milekat.grimtown.proxy.chat.ChatUtils;
import fr.milekat.grimtown.proxy.core.CoreUtils;
import fr.milekat.grimtown.proxy.core.classes.Profile;
import fr.milekat.grimtown.proxy.core.manager.ProfileManager;
import fr.milekat.grimtown.proxy.moderation.managers.MuteManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.HashMap;

public class Reply extends Command {
    private final HashMap<ProxiedPlayer, ProxiedPlayer> PRIVATE_LAST;
    public Reply(HashMap<ProxiedPlayer, ProxiedPlayer> private_last) {
        super("r", "", "reply", "reponse");
        this.PRIVATE_LAST = private_last;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        ProxiedPlayer pSender = ProxyServer.getInstance().getPlayer(sender.getName());
        if (!PRIVATE_LAST.containsKey(pSender)) {
            sender.sendMessage(new TextComponent(CoreUtils.getString("proxy.chat.messages.private.last_not_found")));
            return;
        }
        ProxiedPlayer pDest = PRIVATE_LAST.get(pSender);
        Profile profile = ProfileManager.getProfile(sender.getName());
        if (MuteManager.isMuted(profile)) {
            ChatUtils.warnMute((ProxiedPlayer) sender, MuteManager.getLastMute(profile));
            return;
        }
        if (args.length < 1) {
            sendHelp(sender);
            return;
        }
        if (pDest==null || !pDest.isConnected()) {
            sender.sendMessage(new TextComponent(CoreUtils.getString("proxy.chat.messages.private.last_not_connected")));
            return;
        }
        ChatUtils.sendNewPrivate(pSender, pDest, CoreUtils.getArgsText(0, args));
        PRIVATE_LAST.put(pSender, pDest);
        PRIVATE_LAST.put(pDest, pSender);
    }

    /**
     * Help infos
     */
    private void sendHelp(CommandSender sender){
        sender.sendMessage(new TextComponent(MainBungee.PREFIX + "§6" + getClass().getSimpleName()));
        sender.sendMessage(new TextComponent("§6/r <Message>:§r envoyer message privé au dernier destinataire."));
    }
}
