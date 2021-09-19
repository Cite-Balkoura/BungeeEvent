package fr.milekat.grimtown.proxy.moderation.classes;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.mapping.experimental.MorphiaReference;
import fr.milekat.grimtown.proxy.core.classes.Profile;
import org.bson.types.ObjectId;

import java.util.Date;

@Entity(value = "ban")
public class Ban {
    @Id
    private ObjectId id;
    private MorphiaReference<Profile> profile;
    private Date banDate;
    private Date lastUpdate;
    private Date pardonDate;
    private String reasonBan;
    private String reasonPardon;
    private Boolean acknowledge;

    public Ban() {}

    public Ban(Profile profile, Date banDate, Date pardonDate, String reasonBan) {
        this.profile = MorphiaReference.wrap(profile);
        this.banDate = banDate;
        this.pardonDate = pardonDate;
        this.reasonBan = reasonBan;
        this.lastUpdate = new Date();
    }

    public Profile getProfile() {
        return profile.get();
    }

    public Date getBanDate() {
        return banDate;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public Date getPardonDate() {
        return pardonDate;
    }

    public String getReasonBan() {
        return reasonBan;
    }

    public String getReasonPardon() {
        return reasonPardon;
    }

    public Ban setReasonPardon(String reasonPardon) {
        this.lastUpdate = new Date();
        this.reasonPardon = reasonPardon;
        return this;
    }
}
