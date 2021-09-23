package fr.milekat.grimtown.proxy.chat.events;

import fr.milekat.grimtown.MainBungee;
import fr.milekat.grimtown.utils.RabbitMQ;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

public class Connect implements Listener {
    private final HashMap<UUID, String> MSG_LAST;
    private final HashMap<UUID, Integer> MSG_RECENT;

    public Connect(HashMap<UUID, String> msg_last, HashMap<UUID, Integer> msg_recent) {
        this.MSG_LAST = msg_last;
        this.MSG_RECENT = msg_recent;
    }

    @EventHandler
    public void onProxyJoined(PostLoginEvent event){
        MSG_LAST.remove(event.getPlayer().getUniqueId());
        MSG_RECENT.remove(event.getPlayer().getUniqueId());
        send(MainBungee.getConfig().getString("proxy.login.join")
                .replaceAll("<PLAYER>", event.getPlayer().getName()));
    }

    @EventHandler
    public void onProxyLeave(PlayerDisconnectEvent event) {
        send(MainBungee.getConfig().getString("proxy.login.leave")
                .replaceAll("<PLAYER>", event.getPlayer().getName()));

    }

    /**
     *
     */
    private void send(String msg) {
        try {
            RabbitMQ.rabbitSend(String.format("""
                        {
                            "type": "connectEvent",
                            "event": "%s"
                            "message": "%s"
                        }""", MainBungee.getEvent().getName(), ChatColor.stripColor(msg)));
        } catch (IOException | TimeoutException exception) {
            exception.printStackTrace();
        }
        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            player.sendMessage(new TextComponent(MainBungee.PREFIX + msg));
        }
    }
}
