package studio.overmine.overhub.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import studio.overmine.overhub.OverHub;
import studio.overmine.overhub.controllers.SpawnController;

public class SpawnListener implements Listener {

    private final SpawnController spawnController;

    public SpawnListener(OverHub plugin) {
        this.spawnController = plugin.getSpawnController();
    }

    @EventHandler
    public void onJoinSpawn(PlayerJoinEvent event) {
        spawnController.toSpawn(event.getPlayer());
    }

    @EventHandler
    public void onVoidSpawn(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)
                || !event.getCause().equals(EntityDamageEvent.DamageCause.VOID)) return;

        event.setCancelled(true);

        Player player = (Player) event.getEntity();
        spawnController.toSpawn(player);
    }
}
