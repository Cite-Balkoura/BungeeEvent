package fr.milekat.grimtown.proxy.moderation.events;

import fr.milekat.grimtown.proxy.core.events.RabbitMQReceive;
import fr.milekat.grimtown.proxy.moderation.ModerationUtils;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.UUID;

public class MuteDiscord implements Listener {
    @EventHandler
    public void onDiscordMute(RabbitMQReceive event) {
        if (event.getType().equals(RabbitMQReceive.MessageType.mute)) {
            ModerationUtils.mute(UUID.fromString(String.valueOf(event.getPayload().get("target"))),
                    UUID.fromString(String.valueOf(event.getPayload().get("sender"))),
                    String.valueOf(event.getPayload().get("reason")));
        }
    }

    @EventHandler
    public void onDiscordUnMute(RabbitMQReceive event) {
        if (event.getType().equals(RabbitMQReceive.MessageType.unmute)) {
            ModerationUtils.unMute(UUID.fromString(String.valueOf(event.getPayload().get("target"))));
        }
    }
}
