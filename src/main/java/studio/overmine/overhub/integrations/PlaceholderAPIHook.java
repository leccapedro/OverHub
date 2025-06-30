package studio.overmine.overhub.integrations;

import org.bukkit.Bukkit;

public class PlaceholderAPIHook {

    public static boolean enabled;

    public static void initialize() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            enabled = true;
        }
    }
}
