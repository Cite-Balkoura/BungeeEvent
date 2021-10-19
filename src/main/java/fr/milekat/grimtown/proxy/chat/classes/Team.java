package fr.milekat.grimtown.proxy.chat.classes;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.IndexOptions;
import dev.morphia.annotations.Indexed;
import fr.milekat.grimtown.proxy.core.classes.Profile;
import fr.milekat.grimtown.proxy.core.manager.ProfileManager;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.UUID;

@Entity(value = "team")
public class Team {
    @Id
    private ObjectId id;
    @Indexed(options = @IndexOptions(unique = true, sparse = true))
    private String teamName;
    @Indexed(options = @IndexOptions(unique = true, sparse = true))
    private ArrayList<UUID> members;

    public Team() {}

    public ObjectId getId() {
        return id;
    }

    public String getTeamName() {
        return teamName;
    }

    public ArrayList<Profile> getProfiles() {
        return ProfileManager.getProfiles(members);
    }

    public ArrayList<UUID> getMembers() {
        return members;
    }
}
