package studio.overmine.overhub.controllers;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import studio.overmine.overhub.OverHub;
import studio.overmine.overhub.models.resources.types.ConfigResource;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

@Getter
public class MongoController {

    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> users;
    private boolean connected;

    public MongoController(OverHub plugin) {
        System.setProperty("DEBUG.GO", "true");
        System.setProperty("DB.TRACE", "true");
        Logger.getLogger("org.mongodb.driver")
                .setLevel(Level.WARNING);

        try {
            this.mongoClient = MongoClients.create(ConfigResource.MONGO_URI);
            this.database = mongoClient.getDatabase(ConfigResource.MONGO_DATABASE);
            this.connected = true;
            this.loadCollections();

            plugin.getLogger().info("MongoDB connected.");
        }
        catch (MongoException ex) {
            plugin.getLogger().severe("MongoDB connection failed.");
            Bukkit.getPluginManager().disablePlugin(plugin);
        }
    }

    private void createCollection(String name) {
        if (!database.listCollectionNames().into(new ArrayList<>()).contains(name)) {
            database.createCollection(name);
        }
    }

    private void loadCollections() {
        createCollection("users");
        users = database.getCollection("users");
    }

    public void onDisable() {
        if (connected) {
            mongoClient.close();
            connected = false;
        }
    }
}
