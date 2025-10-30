package studio.overmine.overhub.tasks;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import studio.overmine.overhub.OverHub;
import studio.overmine.overhub.controllers.CombatController;
import studio.overmine.overhub.models.combat.CombatPlayer;

public class CombatModeTask extends BukkitRunnable {

    private final OverHub plugin;
    private final CombatPlayer combatPlayer;
    private final CombatController combatController;

    public CombatModeTask(OverHub plugin, CombatPlayer combatPlayer, CombatController combatController) {
        this.plugin = plugin;
        this.combatPlayer = combatPlayer;
        this.combatController = combatController;
    }

    @Override
    public void run() {
        Player player = combatPlayer.getPlayer();
        if (player == null || !player.isOnline()) {
            combatPlayer.resetCombatState(false);
            cancel();
            if (player != null) {
                combatController.removeCombatPlayer(player);
            }
            return;
        }

        if (!combatPlayer.isInCombat()) {
            combatPlayer.resetCombatState(false);
            cancel();
            return;
        }

        combatPlayer.decrementCombatTimer();

        if (!combatPlayer.isInCombat()) {
            combatPlayer.resetCombatState(false);
            cancel();
        }
    }

    public void start() {
        this.runTaskTimerAsynchronously(plugin, 20L, 20L);
    }
}