package studio.overmine.overhub.controllers;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import studio.overmine.overhub.OverHub;
import studio.overmine.overhub.models.selector.lobby.LobbySelector;
import studio.overmine.overhub.utilities.FileConfig;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Getter
public class LobbySelectorController {

    private final OverHub plugin;
    private final FileConfig lobbySelectorFile;
    private final Map<String, LobbySelector> lobbySelectors;

    public LobbySelectorController(OverHub plugin) {
        this.plugin = plugin;
        this.lobbySelectorFile = plugin.getFileConfig("lobby-selector");
        this.lobbySelectors = new HashMap<>();
        this.onReload();
    }

    public Collection<LobbySelector> getLobbySelectors() {
        return lobbySelectors.values();
    }

    public LobbySelector getLobbySelectorByItem(ItemStack item, Player player) {
        Material itemType = item.getType();

        return getLobbySelectors().stream()
                .filter(lobbySelector -> lobbySelector.getIcon().getType() == itemType)
                .filter(lobbySelector -> lobbySelector.getDisplayIcon(player).isSimilar(item))
                .findFirst()
                .orElse(null);
    }

    public final void onReload() {
        this.lobbySelectors.clear();

        ConfigurationSection lobbiesSection = lobbySelectorFile.getConfiguration().getConfigurationSection("lobbies");
        if (lobbiesSection == null) throw new NullPointerException("Lobbies section is null");

        for (String name : lobbiesSection.getKeys(false)) {
            ConfigurationSection lobbySection = lobbiesSection.getConfigurationSection(name);
            if (lobbySection == null) throw new NullPointerException("Lobby '" + name + "' section is null");

            LobbySelector lobbySelector = new LobbySelector(name, lobbySection);
            lobbySelectors.put(name, lobbySelector);
        }
    }
}
