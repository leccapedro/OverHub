package studio.overmine.overhub.models.resources.types;

import studio.overmine.overhub.OverHub;
import studio.overmine.overhub.models.resources.Resource;
import studio.overmine.overhub.utilities.FileConfig;
import studio.overmine.overhub.utilities.menu.decoration.Decoration;
import studio.overmine.overhub.utilities.menu.decoration.DecorationUtil;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashSet;
import java.util.Set;

public class SelectorResource extends Resource {

    public static String SERVER_SELECTOR_MENU_TITLE;
    public static int SERVER_SELECTOR_MENU_ROWS;
    public static Set<Decoration> SERVER_SELECTOR_MENU_DECORATIONS, LOBBY_SELECTOR_MENU_DECORATIONS;

    public SelectorResource(OverHub plugin) {
        super(plugin);
    }

    @Override
    public void initialize() {
        FileConfig serverSelectorFile = plugin.getFileConfig("server-selector"),
                lobbySelectorFile = plugin.getFileConfig("lobby-selector");
        SERVER_SELECTOR_MENU_TITLE = serverSelectorFile.getString("menu.title", "Server Selector");
        SERVER_SELECTOR_MENU_ROWS = serverSelectorFile.getInt("menu.rows", 3);

        SERVER_SELECTOR_MENU_DECORATIONS = new HashSet<>();

        ConfigurationSection serverSelectorDecorationSection = serverSelectorFile.getConfiguration().getConfigurationSection("menu.decorations");
        DecorationUtil.registerDecorations(serverSelectorDecorationSection, SERVER_SELECTOR_MENU_DECORATIONS);

        LOBBY_SELECTOR_MENU_DECORATIONS = new HashSet<>();

        ConfigurationSection lobbySelectorDecorationSection = lobbySelectorFile.getConfiguration().getConfigurationSection("menu.decorations");
        DecorationUtil.registerDecorations(lobbySelectorDecorationSection, LOBBY_SELECTOR_MENU_DECORATIONS);
    }
}
