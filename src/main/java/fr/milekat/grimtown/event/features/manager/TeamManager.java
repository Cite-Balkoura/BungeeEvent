package fr.milekat.grimtown.event.features.manager;

import dev.morphia.Datastore;
import dev.morphia.query.experimental.filters.Filters;
import fr.milekat.grimtown.MainBungee;
import fr.milekat.grimtown.event.features.classes.Team;
import fr.milekat.grimtown.proxy.core.classes.Profile;
import fr.milekat.grimtown.proxy.core.manager.ProfileManager;
import org.bson.types.ObjectId;

import java.util.UUID;

public class TeamManager {
    private static final Datastore DATASTORE = MainBungee.getDatastore(MainBungee.getEvent().getDatabase());

    /**
     * Get a Team from a member
     */
    public static Team getTeam(ObjectId id) {
        return DATASTORE.find(Team.class)
                .filter(Filters.eq("_id", id))
                .first();
    }

    /**
     * Get a Team from a member
     */
    public static Team getTeam(UUID uuid) {
        return getTeam(ProfileManager.getProfile(uuid));
    }

    /**
     * Get a Team from a member
     */
    public static Team getTeam(Profile profile) {
        return DATASTORE.find(Team.class).stream().filter(team -> team.getProfiles().stream()
                .anyMatch(profile1 -> profile1.getId().equals(profile.getId()))).findFirst().orElse(null);
    }
}
