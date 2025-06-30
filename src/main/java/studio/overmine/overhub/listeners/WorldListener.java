package studio.overmine.overhub.listeners;

import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

public class WorldListener implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        event.setCancelled(event.getPlayer().getGameMode() != GameMode.CREATIVE);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        event.setCancelled(event.getPlayer().getGameMode() != GameMode.CREATIVE);
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        if (event.toWeatherState()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFood(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onExplosion(EntityExplodeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onMobSpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.CUSTOM) {
            event.setCancelled(true);
        }
    }
}
