package fr.milekat.grimtown.proxy.moderation.managers;

import dev.morphia.Datastore;
import dev.morphia.query.experimental.filters.Filters;
import fr.milekat.grimtown.MainBungee;
import fr.milekat.grimtown.proxy.core.classes.Profile;
import fr.milekat.grimtown.proxy.moderation.classes.Ban;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.NoSuchElementException;

public class BanManager {
    private static final Datastore DATASTORE = MainBungee.getDatastore("master");

    /**
     * Check if profile is currently banned (=One of his ban is not acknowledged)
     */
    public static boolean isBanned(Profile profile) {
        return DATASTORE.find(Ban.class).filter(Filters.eq("acknowledge", false)).iterator().toList().
                stream().anyMatch(ban -> ban.getProfile().getId().equals(profile.getId()));
    }

    /**
     * Get last ban of this profile
     */
    public static Ban getLastBan(Profile profile) throws NoSuchElementException {
        return getBans(profile).stream().max(Comparator.comparing(Ban::getLastUpdate)).get();
    }

    /**
     * Get all bans of this profile
     */
    public static ArrayList<Ban> getBans(Profile profile) {
        return new ArrayList<>(DATASTORE.find(Ban.class).filter(Filters.eq("acknowledge", false)).iterator().toList().
                stream().filter(ban -> ban.getProfile().getId().equals(profile.getId())).toList());
    }

    /**
     * Get all bans update from a ban
     */
    public static ArrayList<Ban> getFullBan(Profile profile, Ban ban) {
        return getFullBan(profile, ban.getBanDate());
    }

    /**
     * Get all bans update from a banDate
     */
    public static ArrayList<Ban> getFullBan(Profile profile, Date banDate) {
        return new ArrayList<>(DATASTORE.find(Ban.class)
                .filter(Filters.eq("banDate", banDate))
                .stream().filter(ban -> ban.getProfile().getId().equals(profile.getId()))
                .sorted(Comparator.comparing(Ban::getLastUpdate).reversed()).toList());
    }

    /**
     * Get all members banned
     */
    public static ArrayList<Ban> getBanList() {
        return new ArrayList<>(DATASTORE.find(Ban.class).filter(Filters.eq("acknowledge", false)).iterator().toList());
    }

    /**
     * Save a ban
     */
    public static void save(Ban ban) {
        DATASTORE.save(ban);
    }
}
