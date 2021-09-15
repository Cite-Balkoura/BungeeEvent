package fr.milekat.grimtown.event.classes;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.IndexOptions;
import dev.morphia.annotations.Indexed;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;

@Entity(value = "eventMain")
public class Event {
    @Id
    private ObjectId id;
    @Indexed(options = @IndexOptions(unique = true))
    private String name;
    private String type;
    private ArrayList<EventFeature> eventFeatures;
    private Date startDate;
    private Date maintenanceDate;
    private Date endDate;
    private String description;

    public enum EventFeature {
        TIME,
        OBJECTIVE,
        CITE,
        TEAM
    }

    public Event() {}

    public Event(String name, String type, ArrayList<EventFeature> eventFeatures, Date startDate, Date maintenanceDate, Date endDate, String description) {
        this.name = name;
        this.type = type;
        this.eventFeatures = eventFeatures;
        this.startDate = startDate;
        this.maintenanceDate = maintenanceDate;
        this.endDate = endDate;
        this.description = description;
    }

    public String getName() {
        return name;
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

    public String getDescription() {
        return description;
    }
}