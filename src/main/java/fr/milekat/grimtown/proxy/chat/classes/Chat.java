package fr.milekat.grimtown.proxy.chat.classes;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.UUID;

// TODO: 23/09/2021 Do in Bot
@Entity(value = "chat")
public class Chat {
    @Id
    private ObjectId id;
    private Type type;
    private String message;
    private Date date;
    private UUID sender;
    private UUID receiver;

    enum Type {
        announce,
        connect,
        chat,
        direct,
        team
    }

}
