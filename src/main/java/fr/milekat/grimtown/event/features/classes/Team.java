package fr.milekat.grimtown.event.features.classes;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.IndexOptions;
import dev.morphia.annotations.Indexed;
import dev.morphia.mapping.experimental.MorphiaReference;
import fr.milekat.grimtown.proxy.core.classes.Profile;
import org.bson.types.ObjectId;

import java.util.ArrayList;

@Entity(value = "team")
public class Team {
    @Id
    private ObjectId id;
    @Indexed(options = @IndexOptions(unique = true))
    private String teamName;
    @Indexed(options = @IndexOptions(unique = true))
    private MorphiaReference<ArrayList<Profile>> members;

    public Team() {}

    public String getTeamName() {
        return teamName;
    }

    public ArrayList<Profile> getMembers() {
        return members.get();
    }
}
