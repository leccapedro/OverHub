package studio.overmine.overhub.models.selector.lobby;

import studio.overmine.overhub.utilities.item.ItemBuilder;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Getter
public class LobbySelector {

    private final String name;
    private final ItemStack icon;
    private final int iconSlot;
    private final String lobby;

    public LobbySelector(String name, ConfigurationSection section) {
        this.name = name;
        this.icon = new ItemBuilder(section.getString("item.material"))
                .setDisplayName(section.getString("item.name"))
                .setLore(section.getStringList("item.lore"))
                .setSkullOwner(section.getString("item.head"))
                .setEnchanted(section.getBoolean("item.enchanted"))
                .setModelData(section.getInt("item.model-data"))
                .build();
        this.iconSlot = section.getInt("item.slot");
        this.lobby = section.getString("lobby");
    }

    public ItemStack getDisplayIcon(Player player) {
        ItemStack itemStack = icon.clone();
        return new ItemBuilder(itemStack)
                .setDisplayName(player, itemStack.getItemMeta().getDisplayName())
                .setLore(player, itemStack.getItemMeta().getLore())
                .build();
    }
}
