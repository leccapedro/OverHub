package studio.overmine.overhub.models.resources.types;

import studio.overmine.overhub.OverHub;
import studio.overmine.overhub.models.resources.Resource;
import studio.overmine.overhub.utilities.FileConfig;

import java.util.ArrayList;
import java.util.List;

public class ConfigResource extends Resource {

    public static String MONGO_URI, MONGO_DATABASE;
    public static String SERVER_NAME;
    public static List<String> WELCOME_MESSAGE;

    public ConfigResource(OverHub plugin) {
        super(plugin);
    }

    @Override
    public void initialize() {
        FileConfig configFile = plugin.getFileConfig("config");
        MONGO_URI = configFile.getString("mongo.uri", "mongodb://localhost:27017");
        MONGO_DATABASE = configFile.getString("mongo.database", "OverHub");
        SERVER_NAME = configFile.getString("server-name", "lobby");
        WELCOME_MESSAGE = configFile.getStringList("welcome-message", new ArrayList<>());
    }
}
