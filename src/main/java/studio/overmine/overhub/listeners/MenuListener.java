package studio.overmine.overhub.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import studio.overmine.overhub.utilities.menu.Button;
import studio.overmine.overhub.utilities.menu.Menu;

public class MenuListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onButtonPress(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Menu menu = Menu.getMenu(player);

        if (menu != null) {
            event.setCancelled(!menu.isAllowInteract());

            if (event.getSlot() != event.getRawSlot()) {
                if ((event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT)) {
                    event.setCancelled(!menu.isAllowInteract());
                }
                return;
            }

            if (menu.getButtons().containsKey(event.getSlot()) ) {
                Button button = menu.getButtons().get(event.getSlot());
                boolean shouldCancel = button.shouldCancel(player, event.getSlot(), event.getClick());
                boolean shouldShift = button.shouldShift(player, event.getSlot(), event.getClick());

                if (shouldCancel && shouldShift) {
                    event.setCancelled(true);
                }
                else {
                    event.setCancelled(shouldCancel);
                }

                button.clicked(player, event.getSlot(), event.getClick(), event.getHotbarButton());

                if (Menu.hasMenu(player)) {
                    Menu newMenu = Menu.getMenu(player);

                    if (newMenu == menu) {
                        if (menu.isUpdateAfterClick()) {
                            newMenu.open();
                        }
                    }
                }
                else if (button.shouldUpdate(player, event.getSlot(), event.getClick())) {
                    menu.open();
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        Menu menu = Menu.getMenu(player);

        if (menu != null) {
            menu.close(player);
        }
    }
}