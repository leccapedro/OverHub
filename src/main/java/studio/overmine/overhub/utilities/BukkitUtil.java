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
        SERVER_VERSION = Integer.parseInt(Bukkit.getBukkitVersion().split("\\.")[1]
                .split("-")[0]);
    }
}
