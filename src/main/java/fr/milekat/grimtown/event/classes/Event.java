package fr.milekat.grimtown.event.classes;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.IndexOptions;
import dev.morphia.annotations.Indexed;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;

@Entity(value = "event")
public class Event {
    @Id
    private ObjectId id;
    @Indexed(options = @IndexOptions(unique = true))
    private String name;
    private String database;
    private String type;
    private ArrayList<EventFeature> eventFeatures;
    private Date startDate;
    private Date maintenanceDate;
    private Date endDate;

    public enum EventFeature {
        TIME,
        OBJECTIVE,
        CITE,
        TEAM
    }

    public Event() {}

    public String getName() {
        return name;
    }

    public String getDatabase() {
        return database;
    }

    public String getType() {
        return type;
    }

    public ArrayList<EventFeature> getEventFeatures() {
        return eventFeatures;
    }

    public Date getStartDate() {
        return startDate;
    }

    public boolean isMaintenance() {
        return maintenanceDate.getTime() > new Date().getTime();
    }

    public Date getMaintenanceDate() {
        return maintenanceDate;
    }

    public void setMaintenanceDate(Date maintenanceDate) {
        this.maintenanceDate = maintenanceDate;
    }

    public Date getEndDate() {
        return endDate;
    }
}