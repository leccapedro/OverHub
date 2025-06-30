package studio.overmine.overhub.utilities.menu;

import com.cryptomorin.xseries.XSound;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public abstract class Button {

    public abstract ItemStack getItemStack(Player player);

    public static Button getButton(ItemStack itemStack) {
        return (new Button() {
            public ItemStack getItemStack(Player player) {
                return itemStack.clone();
            }
        });
    }


    public void playNeutral(Player player) {
        XSound.ENTITY_EXPERIENCE_ORB_PICKUP.play(player);
    }

    public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {}

    public boolean shouldUpdate(Player player, int slot, ClickType clickType) {
        return false;
    }

    public boolean shouldCancel(Player player, int slot, ClickType clickType) {
        return true;
    }

    public boolean shouldShift(Player player, int slot, ClickType clickType) {
        return true;
    }
}