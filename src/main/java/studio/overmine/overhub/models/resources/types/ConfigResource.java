package studio.overmine.overhub.models.resources.types;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
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
    public static boolean PVP_MODE_ENABLED;
    public static PvPActivationMode PVP_ACTIVATION_MODE;
    public static int PVP_ACTIVATION_COOLDOWN_SECONDS;
    public static int PVP_COMBAT_DURATION_SECONDS;
    public static int PVP_SWORD_SLOT;
    public static ItemStack PVP_SWORD_ITEM;
    public static int PVP_EXIT_ITEM_SLOT;
    public static ItemStack PVP_EXIT_ITEM;
    public static ItemStack[] PVP_EQUIPMENT;
    public static boolean PVP_SPAWN_ENABLED;
    public static String PVP_SPAWN_LOCATION;
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
        PVP_MODE_ENABLED = configFile.getBoolean("pvp-mode.enabled");
        PVP_ACTIVATION_MODE = PvPActivationMode.fromString(configFile.getString("pvp-mode.activation.mode"));
        PVP_ACTIVATION_COOLDOWN_SECONDS = Math.max(0, configFile.getInt("pvp-mode.activation.cooldown_seconds"));
        PVP_COMBAT_DURATION_SECONDS = Math.max(0, configFile.getInt("pvp-mode.combat.duration_seconds"));
        PVP_SWORD_SLOT = configFile.getInt("pvp-mode.sword.slot");
        PVP_SWORD_ITEM = configFile.getItemStack("pvp-mode.sword");
        PVP_EXIT_ITEM_SLOT = configFile.getInt("pvp-mode.exit-item.slot");
        PVP_EXIT_ITEM = configFile.getItemStack("pvp-mode.exit-item");
        PVP_EQUIPMENT = new ItemStack[]{
                configFile.getItemStack("pvp-mode.equipment.boots"),
                configFile.getItemStack("pvp-mode.equipment.leggings"),
                configFile.getItemStack("pvp-mode.equipment.chestplate"),
                configFile.getItemStack("pvp-mode.equipment.helmet")
        };
        ConfigurationSection spawnSection = configFile.getConfiguration().getConfigurationSection("pvp-mode.spawn");
        if (spawnSection != null) {
            PVP_SPAWN_ENABLED = spawnSection.getBoolean("enabled");
            PVP_SPAWN_LOCATION = spawnSection.getString("location");
        } else {
            PVP_SPAWN_ENABLED = false;
            PVP_SPAWN_LOCATION = "";
        }
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

    public enum PvPActivationMode {
        INSTANT,
        COOLDOWN;

        public static PvPActivationMode fromString(String value) {
            if (value == null) {
                return COOLDOWN;
            }

            try {
                return PvPActivationMode.valueOf(value.trim().toUpperCase());
            } catch (IllegalArgumentException exception) {
                return COOLDOWN;
            }
        }
    }
}
