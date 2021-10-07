package fr.milekat.grimtown.proxy.chat.manager;

import dev.morphia.Datastore;
import dev.morphia.query.experimental.filters.Filters;
import fr.milekat.grimtown.MainBungee;
import fr.milekat.grimtown.proxy.chat.classes.Announce;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class AnnounceManager {
    private static final Datastore DATASTORE = MainBungee.getDatastore(MainBungee.getEvent().getDatabase());

    /**
     * Get a Team from a member
     */
    public static Announce getRandom() {
        ArrayList<Announce> announces = new ArrayList<>(DATASTORE.find(Announce.class)
                .filter(Filters.lte("startDate", new Date()), Filters.gte("endDate", new Date()))
                .iterator().toList());
        if (announces.isEmpty()) return null;
        return announces.get(new Random().nextInt(announces.size()));
    }
}
