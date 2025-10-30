package studio.overmine.overhub.models.combat;

import lombok.Getter;
import org.bukkit.entity.Player;
import studio.overmine.overhub.OverHub;
import studio.overmine.overhub.controllers.CombatController;
import studio.overmine.overhub.models.resources.types.ConfigResource;
import studio.overmine.overhub.models.resources.types.LanguageResource;
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

    private final Player player;
    private CombatStatus status;
    private CombatTask combatTask;
    private CombatModeTask combatModeTask;
    private boolean inCombat;
    private int combatDurationSeconds;
    private int combatTimeRemainingSeconds;
    private long combatStartTimestamp;

    public CombatPlayer(Player player) {
        this.player = player;
        this.status = CombatStatus.EQUIPPING;
        this.inCombat = false;
        this.combatDurationSeconds = 0;
        this.combatTimeRemainingSeconds = 0;
        this.combatStartTimestamp = 0L;
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
        switch (status) {
            case EQUIPPING:
                stopCombatTask();

                status = CombatStatus.UN_EQUIPPING;
                player.getInventory().setArmorContents(ConfigResource.PVP_EQUIPMENT);
                return true;
            case UN_EQUIPPING:
                if (isInCombat()) {
                    ChatUtil.sendMessage(player, LanguageResource.COMBAT_SWORD_MESSAGE_IN_COMBAT
                            .replace("%time%", String.valueOf(getCombatTimeRemainingSeconds())));
                    return false;
                }

                stopCombatTask();
                stopCombatModeTimer();

                combatController.removeCombatPlayer(player);
                player.getInventory().setArmorContents(null);
                return true;
        }

        return false;
    }
}