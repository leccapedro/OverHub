package studio.overmine.overhub.utilities.menu.decoration;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import studio.overmine.overhub.utilities.menu.Button;

/**
 * @author Risas
 * @date 04-07-2025
 * @discord https://risas.me/discord
 */
public class DecorationButton extends Button {

    private final Decoration decoration;

    public DecorationButton(Decoration decoration) {
        this.decoration = decoration;
    }

    @Override
    public ItemStack getItemStack(Player player) {
        return decoration.getItemStack(player).clone();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
        if (decoration.isCommands()) {
            player.closeInventory();
            decoration.getCommands().forEach(command ->
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command
                            .replace("%player%", player.getName())));
        }
    }
}
