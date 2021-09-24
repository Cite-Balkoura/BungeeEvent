package fr.milekat.grimtown.proxy.chat.engine;

import fr.milekat.grimtown.MainBungee;
import fr.milekat.grimtown.proxy.chat.ChatUtils;
import fr.milekat.grimtown.proxy.chat.classes.Announce;
import fr.milekat.grimtown.proxy.chat.classes.Message;
import fr.milekat.grimtown.proxy.chat.manager.AnnounceManager;
import fr.milekat.grimtown.proxy.chat.manager.MessageManager;
import net.md_5.bungee.api.ProxyServer;

import java.util.concurrent.TimeUnit;

public class Announces {
    /**
     * Every X minutes send a new announcement in chat (Start after a minimum of 2 minutes)
     */
    public Announces(Long timer) {
        ProxyServer.getInstance().getScheduler().schedule(MainBungee.getInstance(), () -> {
            if (MessageManager.getLast(Message.Type.chat).getDate().after(MessageManager.getLast(Message.Type.announce).getDate())) {
                Announce announce = AnnounceManager.getRandomAnnounce();
                if (announce!=null) ChatUtils.sendNewAnnounce(announce.getMessage(), null);
                else if (MainBungee.DEBUG_ERRORS) MainBungee.warning("No announce found for now.");
            } else if (MainBungee.DEBUG_ERRORS) MainBungee.warning("No recent chat message found, skip announce.");
        },2L, timer, TimeUnit.MINUTES);
    }
}
