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

    public static String SERVER_SELECTOR_MENU_TITLE, LOBBY_SELECTOR_MENU_TITLE;
    public static int SERVER_SELECTOR_MENU_ROWS, LOBBY_SELECTOR_MENU_ROWS;
    public static Set<Decoration> SERVER_SELECTOR_MENU_DECORATIONS, LOBBY_SELECTOR_MENU_DECORATIONS;
    public static boolean LOBBY_SELECTOR_MENU_MODERN;

    public SelectorResource(OverHub plugin) {
        super(plugin);
    }

    @Override
    public void initialize() {
        FileConfig serverSelectorFile = plugin.getFileConfig("server-selector");
        FileConfig lobbySelectorFile = plugin.getFileConfig("lobby-selector");

        SERVER_SELECTOR_MENU_TITLE = serverSelectorFile.getString("menu.title");
        SERVER_SELECTOR_MENU_ROWS = serverSelectorFile.getInt("menu.rows");
        SERVER_SELECTOR_MENU_DECORATIONS = new HashSet<>();

        ConfigurationSection serverSelectorDecorationSection = serverSelectorFile.getConfiguration().getConfigurationSection("menu.decorations");
        DecorationUtil.registerDecorations(serverSelectorDecorationSection, SERVER_SELECTOR_MENU_DECORATIONS);

        LOBBY_SELECTOR_MENU_TITLE = lobbySelectorFile.getString("menu.title");
        LOBBY_SELECTOR_MENU_ROWS = lobbySelectorFile.getInt("menu.rows");
        LOBBY_SELECTOR_MENU_DECORATIONS = new HashSet<>();

        ConfigurationSection lobbySelectorDecorationSection = lobbySelectorFile.getConfiguration().getConfigurationSection("menu.decorations");
        DecorationUtil.registerDecorations(lobbySelectorDecorationSection, LOBBY_SELECTOR_MENU_DECORATIONS);

        LOBBY_SELECTOR_MENU_MODERN = lobbySelectorFile.getBoolean("menu.modern");
    }
}
