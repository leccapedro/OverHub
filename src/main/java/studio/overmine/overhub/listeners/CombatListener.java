package studio.overmine.overhub.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @author Risas
 * @date 05-07-2025
 * @discord https://risas.me/discord
 */
public class CombatListener implements Listener {

    @EventHandler
    public void onHotbarHeld(PlayerItemHeldEvent event) {
        ItemStack swordItem = event.getPlayer().getInventory().getItem(event.getNewSlot());
        if (swordItem != null && swordItem.getType() == Material.DIAMOND_SWORD) {
            event.getPlayer().sendMessage("empezando cuenta atras 5..");
            //task
        }
    }
}
