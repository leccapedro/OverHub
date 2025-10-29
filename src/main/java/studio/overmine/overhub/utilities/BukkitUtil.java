package studio.overmine.overhub.utilities;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;

/**
 * @author Risas
 * @date 14-07-2025
 * @discord https://risas.me/discord
 */

@UtilityClass
public class BukkitUtil {

    public int SERVER_VERSION;

    static {
        String[] versionParts = Bukkit.getBukkitVersion().split("\\.");
        if (versionParts.length > 1) {
            String[] minorVersionParts = versionParts[1].split("-");
            String minorVersion = minorVersionParts.length > 0 ? minorVersionParts[0] : versionParts[1];
            SERVER_VERSION = minorVersion.isEmpty() ? 0 : Integer.parseInt(minorVersion);
        } else {
            SERVER_VERSION = 0;
        }
    }
}
