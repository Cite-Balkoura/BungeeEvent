package fr.milekat.grimtown.event;

import fr.milekat.grimtown.MainBungee;
import fr.milekat.grimtown.event.classes.Event;
import fr.milekat.grimtown.event.manager.EventManager;

public class ThisEvent {
    private final Event event;

    public ThisEvent() {
        event = EventManager.getEvent(MainBungee.getConfig().getString("core.event"));
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

    public Event getEvent() {
        return this.event;
    }
}
