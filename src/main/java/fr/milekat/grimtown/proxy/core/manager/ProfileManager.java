package fr.milekat.grimtown.proxy.core.manager;

import dev.morphia.Datastore;
import dev.morphia.query.experimental.filters.Filters;
import dev.morphia.query.experimental.updates.UpdateOperators;
import fr.milekat.grimtown.MainBungee;
import fr.milekat.grimtown.proxy.core.classes.Profile;
import net.md_5.bungee.api.connection.ProxiedPlayer;

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
    public static Profile getProfile(ProxiedPlayer player) {
        return DATASTORE.find(Profile.class)
                .filter(Filters.eq("uuid", player.getUniqueId()))
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
    public static boolean notExists(UUID uuid) {
        return DATASTORE.find(Profile.class)
                .filter(Filters.eq("uuid", uuid))
                .first() == null;
    }

    /**
     * Check if Profile exist
     */
    public static boolean notExists(String username) {
        return DATASTORE.find(Profile.class)
                .filter(Filters.eq("username", username))
                .first() == null;
    }

    /**
     * Save/Update a Profile
     */
    public static void updateUsername(UUID uuid, String username) {
        DATASTORE.find(Profile.class)
                .filter(Filters.eq("uuid", uuid))
                .update(UpdateOperators.set("username", username))
                .execute();
    }
}
