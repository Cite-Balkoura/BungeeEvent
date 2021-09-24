package fr.milekat.grimtown.proxy.chat.classes;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.mapping.experimental.MorphiaReference;
import fr.milekat.grimtown.event.features.classes.Team;
import fr.milekat.grimtown.proxy.core.classes.Profile;
import fr.milekat.grimtown.proxy.moderation.managers.MuteManager;
import org.bson.types.ObjectId;

import java.util.Date;

@Entity(value = "message")
public class Message {
    @Id
    private ObjectId id;
    private Type type;
    private String message;
    private Date date;
    private MorphiaReference<Profile> sender;
    private MorphiaReference<Profile> receiver;
    private MorphiaReference<Team> team;
    private boolean muted;
    private MessageRemove remove;

    public Message() {}

    /**
     * Used for type announce sent from console
     */
    public Message(String message) {
        id = new ObjectId();
        type = Type.announce;
        this.message = message;
        date = new Date();
    }

    /**
     * Used for Global chat message
     */
    public Message(String message, Profile sender) {
        id = new ObjectId();
        type = Type.chat;
        this.message = message;
        muted = MuteManager.isMuted(sender);
        this.sender = MorphiaReference.wrap(sender);
        date = new Date();
    }

    /**
     * Used for types announce (Only from player send command), join and leave
     */
    public Message(Type type, String message, Profile sender) {
        id = new ObjectId();
        this.type = type;
        this.message = message;
        this.sender = MorphiaReference.wrap(sender);
        date = new Date();
    }

    /**
     * Used for private messages
     */
    public Message(String message, Profile sender, Profile receiver) {
        id = new ObjectId();
        type = Type.direct;
        this.message = message;
        this.sender = MorphiaReference.wrap(sender);
        this.receiver = MorphiaReference.wrap(receiver);
        date = new Date();
    }

    /**
     * Used for team chat messages
     */
    public Message(String message, Profile sender, Team team) {
        id = new ObjectId();
        type = Type.team;
        this.message = message;
        this.sender = MorphiaReference.wrap(sender);
        this.team = MorphiaReference.wrap(team);
        date = new Date();
    }

     public enum Type {
        announce,
        join,
        leave,
        chat,
        direct,
        team
    }

    public ObjectId getId() {
        return id;
    }

    public Type getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public Date getDate() {
        return date;
    }

    public Profile getSender() {
        return sender.get();
    }

    public Profile getReceiver() {
        return receiver.get();
    }

    public Team getTeam() {
        return team.get();
    }

    public boolean isMuted() {
        return muted;
    }

    public Message remove(Profile remover, String reason) {
        remove = new MessageRemove(remover, reason, new Date());
        return this;
    }

    public MessageRemove getRemove() {
        return remove;
    }

    public boolean isRemoved() {
        return remove!=null;
    }

    private record MessageRemove(Profile remover, String reason, Date date) {}
}
