package studio.overmine.overhub.models.resources.types;

import org.bukkit.configuration.ConfigurationSection;
import studio.overmine.overhub.OverHub;
import studio.overmine.overhub.models.resources.Resource;
import studio.overmine.overhub.utilities.FileConfig;

import java.util.ArrayList;
import java.util.List;

public class ConfigResource extends Resource {

    public static String SERVER_NAME;
    public static List<String> WELCOME_MESSAGE;
    public static boolean CHAT_SYSTEM_ENABLED;
    public static String CHAT_SYSTEM_FORMAT;

    public ConfigResource(OverHub plugin) {
        super(plugin);
    }

    @Override
    public void initialize() {
        FileConfig configFile = plugin.getFileConfig("config");

        SERVER_NAME = configFile.getString("server-name", "lobby");
        WELCOME_MESSAGE = configFile.getStringList("welcome-message", new ArrayList<>());
        CHAT_SYSTEM_ENABLED = configFile.getBoolean("chat-system.enabled", true);
        CHAT_SYSTEM_FORMAT = configFile.getString("chat-system.format", "");
    }
}
