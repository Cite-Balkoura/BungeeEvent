package fr.milekat.grimtown.master.classes;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.IndexOptions;
import dev.morphia.annotations.Indexed;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.UUID;

@Entity(value = "profile")
public class Profile {
    @Id
    private ObjectId id;
    @Indexed(options = @IndexOptions(unique = true))
    private String username;
    @Indexed(options = @IndexOptions(unique = true))
    private UUID uuid;
    @Indexed(options = @IndexOptions(unique = true))
    private long discordId;
    private Date registerDate;

    public Profile() {}

    public Profile(String username, UUID uuid, long discordId, Date registerDate) {
        this.username = username;
        this.uuid = uuid;
        this.discordId = discordId;
        this.registerDate = registerDate;
    }

    public ObjectId getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public UUID getUuid() {
        return uuid;
    }

    public long getDiscordId() {
        return discordId;
    }

    public Date getRegisterDate() {
        return registerDate;
    }
}
