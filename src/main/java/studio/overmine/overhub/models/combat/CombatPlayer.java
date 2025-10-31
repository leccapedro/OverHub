package studio.overmine.overhub.models.combat;

import lombok.Getter;
import org.bukkit.entity.Player;

import studio.overmine.overhub.OverHub;
import studio.overmine.overhub.controllers.CombatController;
import studio.overmine.overhub.controllers.HotbarController;
import studio.overmine.overhub.controllers.SpawnController;
import studio.overmine.overhub.models.resources.types.ConfigResource;
import studio.overmine.overhub.models.resources.types.LanguageResource;
import studio.overmine.overhub.models.user.User;
import studio.overmine.overhub.models.user.types.PvpState;
import studio.overmine.overhub.tasks.CombatModeTask;
import studio.overmine.overhub.tasks.CombatTask;
import studio.overmine.overhub.utilities.ChatUtil;

/**
 * @author Risas
 * @date 06-07-2025
 * @discord https://risas.me/discord
 */

@Getter
public class CombatPlayer {

    private final OverHub plugin;
    private final Player player;
    private CombatStatus status;
    private CombatTask combatTask;
    private CombatModeTask combatModeTask;
    private boolean inCombat;
    private int combatDurationSeconds;
    private int combatTimeRemainingSeconds;
    private long combatStartTimestamp;

    public CombatPlayer(OverHub plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.status = CombatStatus.EQUIPPING;
        this.inCombat = false;
        this.combatDurationSeconds = 0;
        this.combatTimeRemainingSeconds = 0;
        this.combatStartTimestamp = 0L;

        updateUserState(PvpState.EQUIPPING, false);
    }

    public boolean isPvP() {
        return status == CombatStatus.UN_EQUIPPING;
    }

    public boolean isInCombat() {
        return inCombat && combatTimeRemainingSeconds > 0;
    }

    public boolean isCombatTaskRunning() {
        return combatTask != null;
    }

    public void startCombatTask(OverHub plugin, CombatController combatController) {
        if (combatTask != null) {
            combatTask.cancel();
        }

        if (status == CombatStatus.EQUIPPING) {
            updateUserState(PvpState.EQUIPPING, false);
        }
        else {
            updateUserState(PvpState.EXITING, true);
        }

        combatTask = new CombatTask(plugin, this, combatController);
        combatTask.start();
    }

    public void stopCombatTask() {
        if (combatTask != null) {
            combatTask.cancel();
            combatTask = null;
        }
    }

    public void startOrRefreshCombat(OverHub plugin, CombatController combatController) {
        combatDurationSeconds = Math.max(0, ConfigResource.PVP_COMBAT_DURATION_SECONDS);
        if (combatDurationSeconds <= 0) {
            resetCombatState(true);
            return;
        }

        inCombat = true;
        combatTimeRemainingSeconds = combatDurationSeconds;
        combatStartTimestamp = System.currentTimeMillis();

        updateUserState(PvpState.COMBAT, true);

        if (combatModeTask == null) {
            combatModeTask = new CombatModeTask(plugin, this, combatController);
            combatModeTask.start();
        }
    }

    public void decrementCombatTimer() {
        if (!inCombat) {
            return;
        }

        if (combatTimeRemainingSeconds > 0) {
            combatTimeRemainingSeconds--;
        }

        if (combatTimeRemainingSeconds <= 0) {
            inCombat = false;
        }
    }

    public void stopCombatModeTimer() {
        resetCombatState(true);
    }

    public void resetCombatState(boolean cancelTask) {
        inCombat = false;
        combatDurationSeconds = 0;
        combatTimeRemainingSeconds = 0;
        combatStartTimestamp = 0L;

        updateUserState(isPvP() ? PvpState.ACTIVE : PvpState.INACTIVE, isPvP());

        if (cancelTask && combatModeTask != null) {
            combatModeTask.cancel();
        }

        combatModeTask = null;
    }

    public long getCombatStartTimestamp() {
        return combatStartTimestamp;
    }

    public int getCombatDurationSeconds() {
        return combatDurationSeconds;
    }

    public int getCombatTimeRemainingSeconds() {
        return Math.max(0, combatTimeRemainingSeconds);
    }

    public long getCombatTimeRemainingMillis() {
        return getCombatTimeRemainingSeconds() * 1000L;
    }

    public boolean onApplyEquipment(CombatController combatController) {
        HotbarController hotbarController = plugin.getHotbarController();

        switch (status) {
            case EQUIPPING:
                stopCombatTask();

                status = CombatStatus.UN_EQUIPPING;
                boolean hasLayout = hotbarController.applyPvpHotbar(player);
                if (!hasLayout) {
                    ChatUtil.sendMessage(player, LanguageResource.COMBAT_PVP_LAYOUT_MISSING);
                }
                // Armor is now provided by the saved PvP layout (global pvp-inventory.yml)
                updateUserState(PvpState.ACTIVE, true);
                teleportToCombatSpawn();
                return true;
            case UN_EQUIPPING:
                if (isInCombat()) {
                    ChatUtil.sendMessage(player, LanguageResource.COMBAT_SWORD_MESSAGE_IN_COMBAT
                            .replace("%time%", String.valueOf(getCombatTimeRemainingSeconds())));
                    return false;
                }

                stopCombatTask();
                stopCombatModeTimer();

                hotbarController.restoreLobbyHotbar(player);
                combatController.removeCombatPlayer(player);
                player.getInventory().setArmorContents(null);
                status = CombatStatus.EQUIPPING;
                updateUserState(PvpState.INACTIVE, false);
                teleportToSpawn();
                return true;
        }

        return false;
    }

    private User resolveUser() {
        return plugin.getUserController().getUser(player.getUniqueId());
    }

    private void updateUserState(PvpState state, boolean enabled) {
        User user = resolveUser();
        if (user == null) {
            return;
        }

        user.setPvpState(state);
        user.setPvpEnabled(enabled);
    }

    private void teleportToCombatSpawn() {
        SpawnController spawnController = plugin.getSpawnController();
        if (spawnController == null) {
            return;
        }

        spawnController.toCombatSpawn(player);
    }

    private void teleportToSpawn() {
        SpawnController spawnController = plugin.getSpawnController();
        if (spawnController == null) {
            return;
        }

        spawnController.toSpawn(player);
    }
}