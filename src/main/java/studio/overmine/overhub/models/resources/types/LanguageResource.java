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

    public LanguageResource(OverHub plugin) {
        super(plugin);
    }

    @Override
    public void initialize() {
        FileConfig languageFile = plugin.getFileConfig("language");

        SERVER_MESSAGE_ON_JOIN = languageFile.getString("server-message.on-join", "");
        SERVER_MESSAGE_ON_QUIT = languageFile.getString("server-message.on-quit", "");
    }
}
