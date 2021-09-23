package fr.milekat.grimtown.event.features.manager;

import dev.morphia.Datastore;
import dev.morphia.query.experimental.filters.Filters;
import fr.milekat.grimtown.MainBungee;
import fr.milekat.grimtown.event.features.classes.Team;
import fr.milekat.grimtown.proxy.core.classes.Profile;

import java.util.UUID;

public class TeamManager {
    private static final Datastore DATASTORE = MainBungee.getDatastore(MainBungee.getEvent().getDatabase());

    /**
     * Get a Team from a member
     */
    public static Team getTeam(UUID uuid) {
        return getTeam(DATASTORE.find(Profile.class)
                .filter(Filters.eq("uuid", uuid))
                .first());
    }

    /**
     * Get a Team from a member
     */
    public static Team getTeam(Profile profile) {
        return DATASTORE.find(Team.class).stream().filter(team -> team.getMembers().stream()
                .anyMatch(profile1 -> profile1.getId().equals(profile.getId()))).findFirst().orElse(null);
    }
}
