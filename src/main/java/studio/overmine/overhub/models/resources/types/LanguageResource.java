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
            COMBAT_SWORD_MESSAGE_EQUIPPED, COMBAT_SWORD_MESSAGE_UN_EQUIPPED;
    public static String SPAWN_MESSAGE_SET, SPAWN_MESSAGE_TELEPORT;
    public static String PARKOUR_MESSAGE_START, PARKOUR_MESSAGE_ALREADY, PARKOUR_MESSAGE_STREAK, PARKOUR_MESSAGE_FALL,
            PARKOUR_MESSAGE_NEW_HS, PARKOUR_MESSAGE_NOT_CUBOID;

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
        SPAWN_MESSAGE_SET = languageFile.getString("spawn-message.set");
        SPAWN_MESSAGE_TELEPORT = languageFile.getString("spawn-message.teleport");
        PARKOUR_MESSAGE_START = languageFile.getString("parkour-message.start");
        PARKOUR_MESSAGE_ALREADY = languageFile.getString("parkour-message.already");
        PARKOUR_MESSAGE_STREAK = languageFile.getString("parkour-message.streak");
        PARKOUR_MESSAGE_FALL = languageFile.getString("parkour-message.fall");
        PARKOUR_MESSAGE_NEW_HS = languageFile.getString("parkour-message.new-high-score");
        PARKOUR_MESSAGE_NOT_CUBOID = languageFile.getString("parkour-message.area-not-defined");
    }
}
