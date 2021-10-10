package fr.milekat.grimtown.event;

import fr.milekat.grimtown.MainBungee;
import fr.milekat.grimtown.event.classes.Event;

public class EventManager {
    private final Event event;

    public EventManager(Event event) {
        this.event = event;
        MainBungee.info("Loaded event '" + event.getName() + "' successfully.");
        loadFeatures();
    }

    /**
     * Load features listeners and commands
     */
    private void loadFeatures() {
        if (event.getEventFeatures().contains(Event.EventFeature.TEAM)) {

        }
        if (event.getEventFeatures().contains(Event.EventFeature.TIME)) {

        }
        if (event.getEventFeatures().contains(Event.EventFeature.OBJECTIVE)) {

        }
        if (event.getEventFeatures().contains(Event.EventFeature.CITE)) {

        }
    }
}
