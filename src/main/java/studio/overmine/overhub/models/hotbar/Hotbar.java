package studio.overmine.overhub.models.hotbar;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Getter @Setter
public abstract class Hotbar {

    protected final String name;
    protected ItemStack itemStack;
    protected int itemSlot;
    protected boolean unique;

    public Hotbar(String name, boolean unique) {
        this.name = name;
        this.unique = unique;
    }

    public boolean isSimilar(ItemStack toCheck) {
        return (toCheck != null)
                && (toCheck.getType() != Material.AIR)
                && (toCheck.hasItemMeta())
                && (toCheck.getItemMeta().getDisplayName() != null)
                && toCheck.getItemMeta().getDisplayName().equals(itemStack.getItemMeta().getDisplayName());
    }

    public abstract void onAction(Player player);
}
