package fr.milekat.grimtown.proxy.chat.classes;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.IndexOptions;
import dev.morphia.annotations.Indexed;
import org.bson.types.ObjectId;

import java.util.Date;

@Entity(value = "announce")
public class Announce {
    @Id
    private ObjectId id;
    @Indexed(options = @IndexOptions(unique = true))
    private String message;
    private Date startDate;
    private Date endDate;

    public Announce() {}

    public String getMessage() {
        return message;
    }
}
