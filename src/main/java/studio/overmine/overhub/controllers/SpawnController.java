package studio.overmine.overhub.controllers;

import org.bukkit.Bukkit;
import studio.overmine.overhub.OverHub;
import studio.overmine.overhub.utilities.FileConfig;
import studio.overmine.overhub.utilities.SerializeUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SpawnController {

    private final FileConfig configFile;
    private Location spawnLocation;
    private Location combatSpawnLocation;
    private boolean combatSpawnEnabled;

    public SpawnController(OverHub plugin) {
        this.configFile = plugin.getFileConfig("config");
        this.spawnLocation = SerializeUtil.deserializeLocation(configFile.getString("spawn-location"));
        this.combatSpawnEnabled = configFile.getBoolean("combat-spawn.enabled");
        this.combatSpawnLocation = SerializeUtil.deserializeLocation(configFile.getString("combat-spawn.location"));

        if (this.combatSpawnLocation == null) {
            this.combatSpawnEnabled = false;
        }
    }

    public void setSpawnLocation(Location location) {
        this.spawnLocation = location;
        this.configFile.set("spawn-location", SerializeUtil.serializeLocation(location));
        this.configFile.save();
    }

    public void setCombatSpawnLocation(Location location) {
        this.combatSpawnLocation = location;
        this.combatSpawnEnabled = location != null;
        this.configFile.set("combat-spawn.enabled", this.combatSpawnEnabled);
        this.configFile.set("combat-spawn.location", SerializeUtil.serializeLocation(location));
        this.configFile.save();
    }

    public Location getSpawnLocation(Player player) {
        return spawnLocation == null ? player.getWorld().getSpawnLocation() : spawnLocation;
    }

    public Location getCombatSpawn(Player player) {
        if (!combatSpawnEnabled || combatSpawnLocation == null) {
            return getSpawnLocation(player);
        }

        return combatSpawnLocation;
    }

    public void toCombatSpawn(Player player) {
        Bukkit.getScheduler().runTask(OverHub.getPlugin(OverHub.class), () -> player.teleport(getCombatSpawn(player)));
    }

    public void toSpawn(Player player) {
        Bukkit.getScheduler().runTask(OverHub.getPlugin(OverHub.class), () -> player.teleport(getSpawnLocation(player)));
    }
}