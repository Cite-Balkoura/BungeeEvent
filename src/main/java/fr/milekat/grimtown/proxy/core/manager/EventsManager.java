package fr.milekat.grimtown.proxy.core.manager;

import dev.morphia.Datastore;
import dev.morphia.query.experimental.filters.Filters;
import dev.morphia.query.experimental.updates.UpdateOperators;
import fr.milekat.grimtown.MainBungee;
import fr.milekat.grimtown.proxy.core.classes.Event;

import java.util.ArrayList;

public class EventsManager {
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
     * Update maintenanceDate from this event
     */
    public static void updateMaintenance(Event event) {
        DATASTORE.find(Event.class)
                .filter(Filters.eq("name", event.getName()))
                .update(UpdateOperators.set("maintenanceDate", event.getMaintenanceDate()))
                .execute();
    }
}
