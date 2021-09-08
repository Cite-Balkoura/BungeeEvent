package fr.milekat.grimtown.event.manager;

import dev.morphia.Datastore;
import dev.morphia.query.experimental.filters.Filters;
import fr.milekat.grimtown.MainBungee;
import fr.milekat.grimtown.event.classes.Event;

import java.util.ArrayList;

public class EventManager {
    private static final Datastore DATASTORE = MainBungee.getDatastore("master");

    /**
     * Get an Event by his name
     */
    public static Event getEvent(String eventName) {
        return DATASTORE.find(Event.class)
                .filter(Filters.eq("name", eventName))
                .first();
    }

    /**
     * Get all events
     */
    public static ArrayList<Event> getEvents() {
        return new ArrayList<>(DATASTORE.find(Event.class).iterator().toList());
    }

    /**
     * Save/Update an event
     */
    public static void save(Event event) {
        DATASTORE.save(event);
    }
}
