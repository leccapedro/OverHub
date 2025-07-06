package studio.overmine.overhub.models.resources.types;

import org.bukkit.inventory.ItemStack;
import studio.overmine.overhub.OverHub;
import studio.overmine.overhub.models.resources.Resource;
import studio.overmine.overhub.utilities.FileConfig;

import java.util.List;

public class ConfigResource extends Resource {

    public static String SERVER_NAME;
    public static List<String> WELCOME_MESSAGE;
    public static boolean CHAT_SYSTEM_ENABLED;
    public static String CHAT_SYSTEM_FORMAT;
    public static boolean HUB_SWORD_SYSTEM_ENABLED;
    public static int HUB_SWORD_SYSTEM_DELAY, HUB_SWORD_SYSTEM_SLOT;
    public static ItemStack HUB_SWORD_SYSTEM_SWORD;
    public static ItemStack[] HUB_SWORD_SYSTEM_EQUIPMENT;

    public ConfigResource(OverHub plugin) {
        super(plugin);
    }

    @Override
    public void initialize() {
        FileConfig configFile = plugin.getFileConfig("config");

        SERVER_NAME = configFile.getString("server-name");
        WELCOME_MESSAGE = configFile.getStringList("welcome-message");
        CHAT_SYSTEM_ENABLED = configFile.getBoolean("chat-system.enabled");
        CHAT_SYSTEM_FORMAT = configFile.getString("chat-system.format");
        HUB_SWORD_SYSTEM_ENABLED = configFile.getBoolean("hub-sword-system.enabled");
        HUB_SWORD_SYSTEM_DELAY = configFile.getInt("hub-sword-system.delay");
        HUB_SWORD_SYSTEM_SLOT = configFile.getInt("hub-sword-system.sword.slot");
        HUB_SWORD_SYSTEM_SWORD = configFile.getItemStack("hub-sword-system.sword");
        HUB_SWORD_SYSTEM_EQUIPMENT = new ItemStack[]{
                configFile.getItemStack("hub-sword-system.equipment.boots"),
                configFile.getItemStack("hub-sword-system.equipment.leggings"),
                configFile.getItemStack("hub-sword-system.equipment.chestplate"),
                configFile.getItemStack("hub-sword-system.equipment.helmet")
        };
    }
}
