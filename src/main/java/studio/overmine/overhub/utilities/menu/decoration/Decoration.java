package studio.overmine.overhub.utilities.menu.decoration;

import studio.overmine.overhub.utilities.item.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

@Getter @Setter
public class Decoration {

    private int slot;
    private ItemStack itemStack;
    private List<String> commands;

    public ItemStack getItemStack(Player player) {
        ItemStack itemStack = this.itemStack.clone();
        ItemBuilder itemBuilder = new ItemBuilder(itemStack);

        if (itemBuilder.isSkullOwner()) {
            SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
            itemBuilder.setSkullOwner(player, skullMeta.getOwner());
        }

        itemBuilder.setDisplayName(player, itemStack.getItemMeta().getDisplayName());
        itemBuilder.setLore(player, itemStack.getItemMeta().getLore());

        return itemBuilder.build();
    }

    public boolean isCommands() {
        return commands != null && !commands.isEmpty();
    }
}
