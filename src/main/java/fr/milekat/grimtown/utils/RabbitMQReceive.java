package fr.milekat.grimtown.utils;

import net.md_5.bungee.api.plugin.Event;
import org.json.simple.JSONObject;

public class RabbitMQReceive extends Event {
    private final MessageType type;
    private final JSONObject payload;

    public RabbitMQReceive(MessageType type, JSONObject payload) {
        this.type = type;
        this.payload = payload;
    }

    public enum MessageType {
        mute,
        unMute,
        ban,
        unBan,
        chatGlobal,
        chatTeam,
        other
    }

    public MessageType getType() {
        return type;
    }

    public JSONObject getPayload() {
        return payload;
    }
}
