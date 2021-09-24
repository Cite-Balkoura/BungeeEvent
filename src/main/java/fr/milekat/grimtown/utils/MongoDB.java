package fr.milekat.grimtown.utils;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClients;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import dev.morphia.mapping.MapperOptions;
import fr.milekat.grimtown.MainBungee;
import fr.milekat.grimtown.event.classes.Event;
import fr.milekat.grimtown.event.features.classes.Team;
import fr.milekat.grimtown.proxy.chat.classes.Announce;
import fr.milekat.grimtown.proxy.chat.classes.Message;
import fr.milekat.grimtown.proxy.core.classes.Profile;
import fr.milekat.grimtown.proxy.moderation.classes.Ban;
import fr.milekat.grimtown.proxy.moderation.classes.Mute;
import net.md_5.bungee.config.Configuration;
import org.bson.UuidRepresentation;

import java.util.Collections;
import java.util.HashMap;

public class MongoDB {
    /**
     * Load DataStores
     */
    public static HashMap<String, Datastore> getDatastoreMap(Configuration config) {
        HashMap<String, Datastore> datastoreMap = new HashMap<>();
        for (Object dbName : config.getStringList("data.mongo.databases")) {
            if (MainBungee.DEBUG_ERRORS) MainBungee.log("[Mongo] Load db: " + dbName.toString());
            datastoreMap.put(dbName.toString(), setDatastore(config, dbName.toString()));
        }
        if (MainBungee.DEBUG_ERRORS) MainBungee.log("[Mongo] " + datastoreMap.size() + " db loaded");
        return datastoreMap;
    }

    /**
     * MongoDB Connection (Morphia Datastore) to query
     */
    private static Datastore setDatastore(Configuration config, String dbName) {
        MongoCredential credential = MongoCredential.createCredential(
                config.getString("data.mongo.user"),
                config.getString("data.mongo.db"),
                config.getString("data.mongo.password").toCharArray());
        MongoClientSettings settings = MongoClientSettings.builder()
                .uuidRepresentation(UuidRepresentation.JAVA_LEGACY)
                .applyToClusterSettings(builder -> builder.hosts(Collections.singletonList(new ServerAddress(config.getString("data.mongo.host"), config.getInt("data.mongo.port")))))
                .credential(credential)
                .build();
        Datastore datastore = Morphia.createDatastore(MongoClients.create(settings), dbName, MapperOptions.builder()
                .enablePolymorphicQueries(true)
                .build());
        datastore.getMapper().map(Event.class, Team.class);
        datastore.getMapper().map(Announce.class, Message.class);
        datastore.getMapper().map(Profile.class);
        datastore.getMapper().map(Ban.class, Mute.class);
        datastore.ensureIndexes();
        datastore.ensureCaps();
        datastore.enableDocumentValidation();
        return datastore;
    }
}
