package fr.milekat.grimtown.master.managers;

import dev.morphia.Datastore;
import dev.morphia.query.experimental.filters.Filters;
import fr.milekat.grimtown.MainBungee;
import fr.milekat.grimtown.master.classes.Profile;

public class ProfileManager {
    private static final Datastore DATASTORE = MainBungee.getDatastore("master");

    /**
     * Get a Profile by his discord id
     */
    public static Profile getProfile(Long id) {
        return DATASTORE.find(Profile.class)
                .filter(Filters.eq("discordId", id))
                .first();
    }

    /**
     * Check if Profile exist
     */
    public static boolean exists(String username) {
        return DATASTORE.find(Profile.class)
                .filter(Filters.eq("username", username))
                .first()!=null;
    }

    /**
     * Check if Profile exist
     */
    public static boolean exists(Long discordId) {
        return DATASTORE.find(Profile.class)
                .filter(Filters.eq("discordId", discordId))
                .first()!=null;
    }

    /**
     * Save/Update a Profile
     */
    public static void save(Profile profile) {
        DATASTORE.save(profile);
    }
}