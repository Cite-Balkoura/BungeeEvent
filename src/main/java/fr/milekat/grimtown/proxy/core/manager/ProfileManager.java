package fr.milekat.grimtown.proxy.core.manager;

import dev.morphia.Datastore;
import dev.morphia.query.experimental.filters.Filters;
import fr.milekat.grimtown.MainBungee;
import fr.milekat.grimtown.proxy.core.classes.Profile;

import java.util.UUID;

public class ProfileManager {
    private static final Datastore DATASTORE = MainBungee.getDatastore("master");

    /**
     * Get a Profile by his username
     */
    public static Profile getProfile(String username) {
        return DATASTORE.find(Profile.class)
                .filter(Filters.eq("username", username))
                .first();
    }

    /**
     * Get a Profile by his UUID
     */
    public static Profile getProfile(UUID uuid) {
        return DATASTORE.find(Profile.class)
                .filter(Filters.eq("uuid", uuid))
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
     * Save/Update a Profile
     */
    public static void save(Profile profile) {
        DATASTORE.save(profile);
    }
}