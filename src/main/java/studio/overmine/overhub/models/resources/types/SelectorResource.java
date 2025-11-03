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

        Set<Integer> serverOccupiedSlots = new HashSet<>();
        ConfigurationSection serversSection = serverSelectorFile.getConfiguration().getConfigurationSection("servers");
        if (serversSection != null) {
            for (String serverName : serversSection.getKeys(false)) {
                ConfigurationSection serverSection = serversSection.getConfigurationSection(serverName);
                if (serverSection != null && serverSection.contains("item.slot")) {
                    serverOccupiedSlots.add(serverSection.getInt("item.slot"));
                }
            }
        }

        ConfigurationSection serverSelectorDecorationSection = serverSelectorFile.getConfiguration().getConfigurationSection("menu.decorations");
        DecorationUtil.registerDecorations(serverSelectorDecorationSection, SERVER_SELECTOR_MENU_DECORATIONS, SERVER_SELECTOR_MENU_ROWS, serverOccupiedSlots);

        LOBBY_SELECTOR_MENU_TITLE = lobbySelectorFile.getString("menu.title");
        LOBBY_SELECTOR_MENU_ROWS = lobbySelectorFile.getInt("menu.rows");
        LOBBY_SELECTOR_MENU_DECORATIONS = new HashSet<>();

        Set<Integer> lobbyOccupiedSlots = new HashSet<>();
        ConfigurationSection lobbiesSection = lobbySelectorFile.getConfiguration().getConfigurationSection("lobbies");
        if (lobbiesSection != null) {
            for (String lobbyName : lobbiesSection.getKeys(false)) {
                ConfigurationSection lobbySection = lobbiesSection.getConfigurationSection(lobbyName);
                if (lobbySection != null && lobbySection.contains("item.slot")) {
                    lobbyOccupiedSlots.add(lobbySection.getInt("item.slot"));
                }
            }
        }

        ConfigurationSection lobbySelectorDecorationSection = lobbySelectorFile.getConfiguration().getConfigurationSection("menu.decorations");
        DecorationUtil.registerDecorations(lobbySelectorDecorationSection, LOBBY_SELECTOR_MENU_DECORATIONS, LOBBY_SELECTOR_MENU_ROWS, lobbyOccupiedSlots);

        LOBBY_SELECTOR_MENU_MODERN = lobbySelectorFile.getBoolean("menu.modern");
    }
}
