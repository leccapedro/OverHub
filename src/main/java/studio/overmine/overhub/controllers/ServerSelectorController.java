package studio.overmine.overhub.controllers;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

import studio.overmine.overhub.OverHub;
import studio.overmine.overhub.models.selector.server.ServerSelector;
import studio.overmine.overhub.utilities.FileConfig;
import lombok.Getter;

@Getter
public class ServerSelectorController {

    private final OverHub plugin;
    private final FileConfig serverSelectorFile;
    private final Map<String, ServerSelector> serverSelectors;

    public ServerSelectorController(OverHub plugin) {
        this.plugin = plugin;
        this.serverSelectorFile = plugin.getFileConfig("server-selector");
        this.serverSelectors = new HashMap<>();
        this.onReload();
    }

    public Collection<ServerSelector> getServerSelectors() {
        return serverSelectors.values();
    }

    public final void onReload() {
        this.serverSelectors.clear();

        ConfigurationSection serversSection = serverSelectorFile.getConfiguration().getConfigurationSection("servers");
        if (serversSection == null) throw new NullPointerException("Servers section in server-selector.yml is null");

        for (String serverName : serversSection.getKeys(false)) {
            ConfigurationSection serverSection = serversSection.getConfigurationSection(serverName);
            if (serverSection == null) throw new NullPointerException("Server '" + serverName + "' section is null");

            ServerSelector serverSelector = new ServerSelector(plugin, serverName, serverSection);
            serverSelectors.put(serverName, serverSelector);
        }
    }
}
