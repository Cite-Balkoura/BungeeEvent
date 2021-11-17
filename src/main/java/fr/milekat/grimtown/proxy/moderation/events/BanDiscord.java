package fr.milekat.grimtown.proxy.moderation.events;

import fr.milekat.grimtown.proxy.moderation.ModerationUtils;
import fr.milekat.grimtown.utils.RabbitMQReceive;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.UUID;

public class BanDiscord implements Listener {
    @EventHandler
    public void onDiscordBan(RabbitMQReceive event) {
        if (event.getType().equals(RabbitMQReceive.MessageType.ban)) {
            ModerationUtils.ban(UUID.fromString(String.valueOf(event.getPayload().get("target"))),
                    UUID.fromString(String.valueOf(event.getPayload().get("sender"))),
                    Long.parseLong(String.valueOf(event.getPayload().get("delay"))),
                    String.valueOf(event.getPayload().get("reason")));
        }
    }

    @EventHandler
    public void onDiscordUnBan(RabbitMQReceive event) {
        if (event.getType().equals(RabbitMQReceive.MessageType.unBan)) {
            ModerationUtils.unBan(UUID.fromString(String.valueOf(event.getPayload().get("target"))));
        }
    }
}
