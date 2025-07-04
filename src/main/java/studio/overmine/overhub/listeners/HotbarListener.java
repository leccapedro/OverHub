package studio.overmine.overhub.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import studio.overmine.overhub.OverHub;
import studio.overmine.overhub.controllers.HotbarController;
import studio.overmine.overhub.models.hotbar.Hotbar;

public class HotbarListener implements Listener {

    private final HotbarController hotbarController;

    public HotbarListener(OverHub plugin) {
        this.hotbarController = plugin.getHotbarController();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        hotbarController.giveHotbar(event.getPlayer());
    }

    @EventHandler
    public void onHotbarInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) return;

        Hotbar hotbar = hotbarController.getHotbarByItem(event.getItem());
        if (hotbar == null) return;

        event.setCancelled(true);
        hotbar.onAction(event.getPlayer());
    }
}
