package studio.overmine.overhub.models.resources.types;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import studio.overmine.overhub.OverHub;
import studio.overmine.overhub.models.resources.Resource;
import studio.overmine.overhub.utilities.FileConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ConfigResource extends Resource {

    public static String SERVER_NAME;
    public static List<String> WELCOME_MESSAGE;
    public static boolean CHAT_SYSTEM_ENABLED;
    public static String CHAT_SYSTEM_FORMAT;
    public static boolean HUB_SWORD_SYSTEM_ENABLED;
    public static int HUB_SWORD_SYSTEM_DELAY, HUB_SWORD_SYSTEM_SLOT;
    public static ItemStack HUB_SWORD_SYSTEM_SWORD;
    public static ItemStack[] HUB_SWORD_SYSTEM_EQUIPMENT;
    public static boolean BOSS_BAR_SYSTEM_ENABLED;
    public static boolean PARKOUR_SYSTEM_ENABLED;
    public static List<String> PARKOUR_SYSTEM_STREAK_COMMANDS;
    public static int PARKOUR_SYSTEM_STREAK_INTERVAL, PARKOUR_SYSTEM_GENERATOR_ATTEMPTS, PARKOUR_SYSTEM_GENERATOR_DISTANCE_MIN,
            PARKOUR_SYSTEM_GENERATOR_DISTANCE_MAX;
    public static List<Material> PARKOUR_SYSTEM_GENERATOR_BLOCKS;

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
        BOSS_BAR_SYSTEM_ENABLED = configFile.getBoolean("boss-bar-system.enabled");
        PARKOUR_SYSTEM_ENABLED = configFile.getBoolean("parkour-system.enabled");
        PARKOUR_SYSTEM_STREAK_COMMANDS = configFile.getStringList("parkour-system.streak.commands");
        PARKOUR_SYSTEM_STREAK_INTERVAL = configFile.getInt("parkour-system.streak.points-intervals");
        PARKOUR_SYSTEM_GENERATOR_ATTEMPTS = configFile.getInt("parkour-system.generator.attempts");
        PARKOUR_SYSTEM_GENERATOR_DISTANCE_MIN = configFile.getInt("parkour-system.generator.min-distance");
        PARKOUR_SYSTEM_GENERATOR_DISTANCE_MAX = configFile.getInt("parkour-system.generator.max-distance");
        PARKOUR_SYSTEM_GENERATOR_BLOCKS = new ArrayList<>();
        for (String blockName : configFile.getStringList("parkour-system.generator.block-materials")) {
            Optional<XMaterial> material = XMaterial.matchXMaterial(blockName);
            if (!material.isPresent()) {
                Bukkit.getLogger().warning("Invalid block name: " + blockName);
                continue;
            }
            PARKOUR_SYSTEM_GENERATOR_BLOCKS.add(material.get().parseMaterial());
        }
    }
}
