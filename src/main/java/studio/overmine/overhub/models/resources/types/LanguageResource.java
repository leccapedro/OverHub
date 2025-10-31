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
            COMBAT_PVP_LAYOUT_DENIED, COMBAT_PVP_EXIT_IN_COMBAT;
    public static String SPAWN_MESSAGE_SET, SPAWN_MESSAGE_TELEPORT;
    public static String PARKOUR_MESSAGE_START, PARKOUR_MESSAGE_STREAK, PARKOUR_MESSAGE_FALL,
            PARKOUR_MESSAGE_NEW_HS, PARKOUR_MESSAGE_NOT_CUBOID, PARKOUR_MESSAGE_ERROR;
    public static String VISIBILITY_MESSAGE_COOLDOWN;
    public static String OVERHUB_RELOAD_MESSAGE;

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
    }
}
