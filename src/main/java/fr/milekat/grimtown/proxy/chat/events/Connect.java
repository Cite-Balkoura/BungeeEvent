package fr.milekat.grimtown.proxy.chat.events;

import fr.milekat.grimtown.MainBungee;
import fr.milekat.grimtown.proxy.chat.ChatUtils;
import fr.milekat.grimtown.proxy.chat.classes.Message;
import fr.milekat.grimtown.proxy.core.manager.ProfileManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

public class Connect implements Listener {
    private final HashMap<UUID, String> MSG_LAST;
    private final HashMap<UUID, Integer> MSG_RECENT;

    public Connect(HashMap<UUID, String> msg_last, HashMap<UUID, Integer> msg_recent) {
        this.MSG_LAST = msg_last;
        this.MSG_RECENT = msg_recent;
    }

    @EventHandler
    public void onProxyJoined(PostLoginEvent event) {
        MSG_LAST.remove(event.getPlayer().getUniqueId());
        MSG_RECENT.remove(event.getPlayer().getUniqueId());
        ChatUtils.sendNewConnection(new Message(Message.Type.join, MainBungee.getConfig().getString("proxy.login.join")
                .replaceAll("<PLAYER>", event.getPlayer().getName()),
                ProfileManager.getProfile(event.getPlayer())));
    }

    @EventHandler
    public void onServerJoined(ServerConnectedEvent event) {
        ProxyServer.getInstance().getScheduler().runAsync(MainBungee.getInstance(), ()->
                ChatUtils.sendMessages(15, Collections.singleton(event.getPlayer()))
        );
    }

    @EventHandler
    public void onProxyLeave(PlayerDisconnectEvent event) {
        ChatUtils.sendNewConnection(new Message(Message.Type.leave, MainBungee.getConfig().getString("proxy.login.leave")
                .replaceAll("<PLAYER>", event.getPlayer().getName()),
                ProfileManager.getProfile(event.getPlayer())));
    }
}
