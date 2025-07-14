package studio.overmine.overhub.models.selector.server;

import studio.overmine.overhub.OverHub;
import studio.overmine.overhub.utilities.FileConfig;
import studio.overmine.overhub.utilities.item.ItemBuilder;
import studio.overmine.overhub.utilities.menu.decoration.Decoration;
import studio.overmine.overhub.utilities.menu.decoration.DecorationUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@Getter
public class ServerSelector {

    private final String menuTitle;
    private final int menuRows;
    private final Set<Decoration> menuDecorations;

    private final String name;
    private final ItemStack icon;
    private final int iconSlot;
    private final String server;
    private final List<String> commands;
    private final List<SubServerSelector> subServerSelectors;
    private final FileConfig subServerFile;

    public ServerSelector(OverHub plugin, String name, ConfigurationSection section) {
        this.name = name;
        this.icon = new ItemBuilder(section.getString("item.material"))
                .setDisplayName(section.getString("item.name"))
                .setLore(section.getStringList("item.lore"))
                .setSkullOwner(section.getString("item.head"))
                .setEnchanted(section.getBoolean("item.enchanted"))
                .setModelData(section.getInt("item.model-data"))
                .build();
        this.iconSlot = section.getInt("item.slot");
        this.server = section.getString("server");
        this.commands = section.getStringList("commands");
        this.subServerSelectors = new ArrayList<>();
        this.subServerFile = new FileConfig(plugin, "selector/server/sub-server/" + name + "-server.yml");

        this.loadOrCreateFolder();

        this.menuTitle = subServerFile.getString("menu.title");
        this.menuRows = subServerFile.getInt("menu.rows");
        this.menuDecorations = new HashSet<>();

        ConfigurationSection serverSelectorDecorationSection = subServerFile.getConfiguration().getConfigurationSection("menu.decorations");
        DecorationUtil.registerDecorations(serverSelectorDecorationSection, menuDecorations);

        ConfigurationSection subServersSection = subServerFile.getConfiguration().getConfigurationSection("sub-servers");
        if (subServersSection == null) throw new NullPointerException("Server '" + name + "' sub-servers section is null");

        for (String subServerName : subServersSection.getKeys(false)) {
            ConfigurationSection subServerSection = subServersSection.getConfigurationSection(subServerName);
            if (subServerSection == null) throw new NullPointerException("Server '" + name + "' sub-server '" + subServerName + "' section is null");

            SubServerSelector subServerSelector = new SubServerSelector(subServerName, name, subServerSection);
            subServerSelectors.add(subServerSelector);
        }
    }

    public void loadOrCreateFolder() {
        FileConfiguration configuration = subServerFile.getConfiguration();

        if (configuration.getKeys(true).isEmpty()) {
            configuration.set("menu.title", "%server% Server Selector");
            configuration.set("menu.rows", 3);
            configuration.set("menu.decorations", new HashMap<>());
            configuration.set("sub-servers", new HashMap<>());
            subServerFile.save();
        }
    }

    public void executeCommands(Player player) {
        commands.forEach(command -> {
            boolean isPlayer = command.startsWith("player:");

            if (isPlayer) {
                Bukkit.dispatchCommand(player, command
                        .replace("player:", ""));
            }
            else {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command
                        .replace("%player%", player.getName()));
            }
        });
    }
}
