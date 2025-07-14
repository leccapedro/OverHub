package studio.overmine.overhub.models.selector.server;

import studio.overmine.overhub.utilities.item.ItemBuilder;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@Getter
public class SubServerSelector {

    private final String name;
    private final ItemStack icon;
    private final int iconSlot;
    private final String subServer, server;
    private final List<String> commands;

    public SubServerSelector(String name, String server,ConfigurationSection section) {
        this.name = name;
        this.server = server;
        this.icon = new ItemBuilder(section.getString("item.material"))
                .setDisplayName(section.getString("item.name"))
                .setLore(section.getStringList("item.lore"))
                .setSkullOwner(section.getString("item.head"))
                .setEnchanted(section.getBoolean("item.enchanted"))
                .setModelData(section.getInt("item.model-data"))
                .build();
        this.iconSlot = section.getInt("item.slot");
        this.subServer = section.getString("sub-server");
        this.commands = section.getStringList("commands");
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
