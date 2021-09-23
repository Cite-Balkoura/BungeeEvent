package fr.milekat.grimtown.proxy.chat.engine;

import fr.milekat.grimtown.MainBungee;
import net.md_5.bungee.api.ProxyServer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Spam {
    public Spam(HashMap<UUID, Integer> msg_recent, Long timer) {
        ProxyServer.getInstance().getScheduler().schedule(MainBungee.getInstance(), () -> {
            for (Map.Entry<UUID, Integer> loop : msg_recent.entrySet()) {
                int val = Integer.parseInt(loop.getValue().toString()) - 3;
                if (val < 0) val = 0;
                loop.setValue(val);
            }
        }, 0L, timer, TimeUnit.MILLISECONDS);
    }
}
