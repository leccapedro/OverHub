package studio.overmine.overhub.controllers;

import org.bukkit.entity.Player;
import studio.overmine.overhub.OverHub;
import studio.overmine.overhub.models.combat.CombatPlayer;
import studio.overmine.overhub.models.resources.types.ConfigResource;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CombatController {

    private final OverHub plugin;
    private final Map<UUID, CombatPlayer> combats;

    public CombatController(OverHub plugin) {
        this.plugin = plugin;
        this.combats = new ConcurrentHashMap<>();
    }

    public CombatPlayer getCombatPlayer(Player player) {
        return combats.get(player.getUniqueId());
    }

    public void addCombatPlayer(Player player) {
        if (!ConfigResource.PVP_MODE_ENABLED) {
            return;
        }

        CombatPlayer combatPlayer = new CombatPlayer(plugin, player);
        combats.put(player.getUniqueId(), combatPlayer);
        combatPlayer.startCombatTask(plugin, this);
    }

    public void removeCombatPlayer(Player player) {
        CombatPlayer combatPlayer = combats.remove(player.getUniqueId());
        if (combatPlayer == null) {
            return;
        }

        combatPlayer.stopCombatTask();
        combatPlayer.stopCombatModeTimer();
        plugin.getHotbarController().discardLobbySnapshot(player.getUniqueId());
    }
}
