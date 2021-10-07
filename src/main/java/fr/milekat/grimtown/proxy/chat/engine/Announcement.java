package fr.milekat.grimtown.proxy.chat.engine;

import fr.milekat.grimtown.MainBungee;
import fr.milekat.grimtown.proxy.chat.ChatUtils;
import fr.milekat.grimtown.proxy.chat.classes.Announce;
import fr.milekat.grimtown.proxy.chat.classes.Message;
import fr.milekat.grimtown.proxy.chat.manager.AnnounceManager;
import fr.milekat.grimtown.proxy.chat.manager.MessageManager;
import net.md_5.bungee.api.ProxyServer;

import java.util.concurrent.TimeUnit;

public class Announcement {
    /**
     * Every X minutes send a new announcement in chat (Start after a minimum of 2 minutes)
     */
    public Announcement(Long timer) {
        ProxyServer.getInstance().getScheduler().schedule(MainBungee.getInstance(), () -> {
            Message chatMsg = MessageManager.getLast(Message.Type.chat);
            Message announceMsg = MessageManager.getLast(Message.Type.announce);
            if (chatMsg == null) {
                if (MainBungee.DEBUG_ERRORS) MainBungee.warning("No recent chat message found, skip announce.");
            } else if (announceMsg==null || chatMsg.getDate().after(announceMsg.getDate())) {
                Announce announce = AnnounceManager.getRandom();
                if (announce!=null) ChatUtils.sendNewAnnounce(announce.getMessage(), null);
                else if (MainBungee.DEBUG_ERRORS) MainBungee.warning("No announce found for now.");
            } else if (MainBungee.DEBUG_ERRORS) MainBungee.warning("A recent announce has been found, skip announce.");
        },2L, timer, TimeUnit.MINUTES);
    }
}
