package fr.milekat.grimtown.proxy.chat.manager;

import dev.morphia.Datastore;
import dev.morphia.query.FindOptions;
import dev.morphia.query.Sort;
import dev.morphia.query.experimental.filters.Filters;
import dev.morphia.query.experimental.updates.UpdateOperators;
import fr.milekat.grimtown.MainBungee;
import fr.milekat.grimtown.proxy.chat.classes.Message;

import java.util.ArrayList;

public class MessageManager {
    private static final Datastore DATASTORE = MainBungee.getDatastore(MainBungee.getEvent().getDatabase());

    public static Message getMessage(String id) {
        return DATASTORE.find(Message.class).filter(Filters.eq("id", id)).first();
    }

    /**
     * Get X last messages
     */
    public static ArrayList<Message> getLast(int count) {
        return new ArrayList<>(DATASTORE.find(Message.class)
                .iterator(new FindOptions().sort(Sort.ascending("id")).limit(count)).toList());
    }

    /**
     * Get last message of type
     */
    public static Message getLast(Message.Type type) {
        return DATASTORE.find(Message.class).filter(Filters.eq("type", type))
                .iterator(new FindOptions().sort(Sort.ascending("id")).limit(1)).tryNext();
    }

    /**
     * Inter a new message into collection
     */
    public static void save(Message message) {
        DATASTORE.save(message);
    }

    /**
     * Update remove state
     */
    public static void updateRemove(Message message) {
        DATASTORE.find(Message.class).filter(Filters.eq("id", message.getId()))
                .update(UpdateOperators.set("remove", message.getRemove()))
                .execute();
    }
}
