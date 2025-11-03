package studio.overmine.overhub.models.resources.types;

import studio.overmine.overhub.OverHub;
import studio.overmine.overhub.models.resources.Resource;
import studio.overmine.overhub.utilities.FileConfig;

/**
 * @author Risas
 * @date 04-07-2025
 * @discord https://risas.me/discord
 */
public class LanguageResource extends Resource {

    public static String SERVER_MESSAGE_ON_JOIN, SERVER_MESSAGE_ON_QUIT;
    public static String COMBAT_SWORD_MESSAGE_EQUIPPING, COMBAT_SWORD_MESSAGE_UN_EQUIPPING,
            COMBAT_SWORD_MESSAGE_EQUIPPED, COMBAT_SWORD_MESSAGE_UN_EQUIPPED,
            COMBAT_SWORD_MESSAGE_IN_COMBAT;
    public static String COMBAT_PVP_LAYOUT_SAVED, COMBAT_PVP_LAYOUT_MISSING,
            COMBAT_PVP_LAYOUT_DENIED, COMBAT_PVP_EXIT_IN_COMBAT, COMBAT_PVP_KILL, COMBAT_PVP_DEATH;
    public static String SPAWN_MESSAGE_SET, SPAWN_MESSAGE_TELEPORT;
    public static String PARKOUR_MESSAGE_START, PARKOUR_MESSAGE_STREAK, PARKOUR_MESSAGE_FALL,
            PARKOUR_MESSAGE_NEW_HS, PARKOUR_MESSAGE_NOT_CUBOID, PARKOUR_MESSAGE_ERROR;
    public static String VISIBILITY_MESSAGE_COOLDOWN;
    public static String OVERHUB_RELOAD_MESSAGE;
    
    public static String GAMEMODE_MESSAGE_GAMEMODE, GAMEMODE_MESSAGE_GAMEMODE_CHANGE, GAMEMODE_MESSAGE_PLAYER_CHANGE_GAMEMODE;
    public static String FLY_MESSAGE_ENABLED, FLY_MESSAGE_DISABLED, FLY_MESSAGE_ENABLED_OTHER, FLY_MESSAGE_ENABLED_PLAYER, FLY_MESSAGE_DISABLED_OTHER, FLY_MESSAGE_DISABLED_PLAYER;
    public static String HEALTH_MESSAGE_FULL, HEALTH_MESSAGE_SUCCESSFULLY, HEALTH_MESSAGE_OTHER_SUCCESSFULLY, HEALTH_MESSAGE_PLAYER_SUCCESSFULLY;
    public static String FEED_MESSAGE_FULL, FEED_MESSAGE_SUCCESSFULLY, FEED_MESSAGE_OTHER_SUCCESSFULLY, FEED_MESSAGE_PLAYER_SUCCESSFULLY;
    public static String CLEAR_INVENTORY_MESSAGE_CLEAR_INVENTORY, CLEAR_INVENTORY_MESSAGE_CLEAR_INVENTORY_OTHER, CLEAR_INVENTORY_MESSAGE_CLEAR_INVENTORY_PLAYER;
    public static String TPALL_MESSAGE_PLAYERS, TPALL_MESSAGE_TPALL;
    public static String TP_MESSAGE_TP_MESSAGE, TP_MESSAGE_TPHERE_MESSAGE;

    public LanguageResource(OverHub plugin) {
        super(plugin);
    }

    @Override
    public void initialize() {
        FileConfig languageFile = plugin.getFileConfig("language");

        SERVER_MESSAGE_ON_JOIN = languageFile.getString("server-message.on-join");
        SERVER_MESSAGE_ON_QUIT = languageFile.getString("server-message.on-quit");
        COMBAT_SWORD_MESSAGE_EQUIPPING = languageFile.getString("combat-sword-message.equipping");
        COMBAT_SWORD_MESSAGE_UN_EQUIPPING = languageFile.getString("combat-sword-message.un-equipping");
        COMBAT_SWORD_MESSAGE_EQUIPPED = languageFile.getString("combat-sword-message.equipped");
        COMBAT_SWORD_MESSAGE_UN_EQUIPPED = languageFile.getString("combat-sword-message.un-equipped");
        COMBAT_SWORD_MESSAGE_IN_COMBAT = languageFile.getString("combat-sword-message.in-combat");
        COMBAT_PVP_LAYOUT_SAVED = languageFile.getString("combat-pvp-message.layout-saved");
        COMBAT_PVP_LAYOUT_MISSING = languageFile.getString("combat-pvp-message.layout-missing");
        COMBAT_PVP_LAYOUT_DENIED = languageFile.getString("combat-pvp-message.layout-denied");
        COMBAT_PVP_EXIT_IN_COMBAT = languageFile.getString("combat-pvp-message.exit-in-combat");
        COMBAT_PVP_KILL = languageFile.getString("combat-pvp-message.kill");
        COMBAT_PVP_DEATH = languageFile.getString("combat-pvp-message.death");
        SPAWN_MESSAGE_SET = languageFile.getString("spawn-message.set");
        SPAWN_MESSAGE_TELEPORT = languageFile.getString("spawn-message.teleport");
        PARKOUR_MESSAGE_START = languageFile.getString("parkour-message.start");
        PARKOUR_MESSAGE_STREAK = languageFile.getString("parkour-message.streak");
        PARKOUR_MESSAGE_FALL = languageFile.getString("parkour-message.fall");
        PARKOUR_MESSAGE_NEW_HS = languageFile.getString("parkour-message.new-high-score");
        PARKOUR_MESSAGE_NOT_CUBOID = languageFile.getString("parkour-message.area-not-defined");
        PARKOUR_MESSAGE_ERROR = languageFile.getString("parkour-message.error");
        VISIBILITY_MESSAGE_COOLDOWN = languageFile.getString("visibility-message.cooldown");
        OVERHUB_RELOAD_MESSAGE = languageFile.getString("overhub-message.reload");
        
        GAMEMODE_MESSAGE_GAMEMODE = languageFile.getString("gamemode-message.gamemode");
        GAMEMODE_MESSAGE_GAMEMODE_CHANGE = languageFile.getString("gamemode-message.gamemode-change");
        GAMEMODE_MESSAGE_PLAYER_CHANGE_GAMEMODE = languageFile.getString("gamemode-message.player-change-gamemode");
        FLY_MESSAGE_ENABLED = languageFile.getString("fly-message.enabled");
        FLY_MESSAGE_DISABLED = languageFile.getString("fly-message.disabled");
        FLY_MESSAGE_ENABLED_OTHER = languageFile.getString("fly-message.enabled-other");
        FLY_MESSAGE_ENABLED_PLAYER = languageFile.getString("fly-message.enabled-player");
        FLY_MESSAGE_DISABLED_OTHER = languageFile.getString("fly-message.disabled-other");
        FLY_MESSAGE_DISABLED_PLAYER = languageFile.getString("fly-message.disabled-player");
        HEALTH_MESSAGE_FULL = languageFile.getString("health-message.full");
        HEALTH_MESSAGE_SUCCESSFULLY = languageFile.getString("health-message.successfully");
        HEALTH_MESSAGE_OTHER_SUCCESSFULLY = languageFile.getString("health-message.other-successfully");
        HEALTH_MESSAGE_PLAYER_SUCCESSFULLY = languageFile.getString("health-message.player-successfully");
        FEED_MESSAGE_FULL = languageFile.getString("feed-message.full");
        FEED_MESSAGE_SUCCESSFULLY = languageFile.getString("feed-message.successfully");
        FEED_MESSAGE_OTHER_SUCCESSFULLY = languageFile.getString("feed-message.other-successfully");
        FEED_MESSAGE_PLAYER_SUCCESSFULLY = languageFile.getString("feed-message.player-successfully");
        CLEAR_INVENTORY_MESSAGE_CLEAR_INVENTORY = languageFile.getString("clear-inventory-message.clear-inventory");
        CLEAR_INVENTORY_MESSAGE_CLEAR_INVENTORY_OTHER = languageFile.getString("clear-inventory-message.clear-inventory-other");
        CLEAR_INVENTORY_MESSAGE_CLEAR_INVENTORY_PLAYER = languageFile.getString("clear-inventory-message.clear-inventory-player");
        TPALL_MESSAGE_PLAYERS = languageFile.getString("tpall-message.players");
        TPALL_MESSAGE_TPALL = languageFile.getString("tpall-message.tpall");
        TP_MESSAGE_TP_MESSAGE = languageFile.getString("tp-message.tp-message");
        TP_MESSAGE_TPHERE_MESSAGE = languageFile.getString("tp-message.tphere-message");
    }
}
