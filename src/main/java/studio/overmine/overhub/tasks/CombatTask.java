package studio.overmine.overhub.tasks;

import com.cryptomorin.xseries.XSound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import studio.overmine.overhub.OverHub;
import studio.overmine.overhub.controllers.CombatController;
import studio.overmine.overhub.models.combat.CombatPlayer;
import studio.overmine.overhub.models.resources.types.ConfigResource;
import studio.overmine.overhub.models.resources.types.LanguageResource;
import studio.overmine.overhub.utilities.ChatUtil;

public class CombatTask extends BukkitRunnable {

    private final OverHub plugin;
    private final CombatPlayer combatPlayer;
    private final CombatController combatController;
    private int countdown;

    public CombatTask(OverHub plugin, CombatPlayer combatPlayer, CombatController combatController) {
        this.plugin = plugin;
        this.combatPlayer = combatPlayer;
        this.combatController = combatController;
        this.countdown = getInitialCountdown();
    }

    @Override
    public void run() {
        Player player = combatPlayer.getPlayer();

        switch (combatPlayer.getStatus()) {
            case EQUIPPING:
                if (!isCooldownMode() || countdown <= 0) {
                    completeEquipping(player);
                    return;
                }

                XSound.BLOCK_NOTE_BLOCK_BANJO.play(player, 1F, 1F);
                ChatUtil.sendMessage(player, LanguageResource.COMBAT_SWORD_MESSAGE_EQUIPPING
                        .replace("%countdown%", String.valueOf(countdown)));
                break;
            case UN_EQUIPPING:
                if (!isCooldownMode() || countdown <= 0) {
                    completeUnEquipping(player);
                    return;
                }

                XSound.BLOCK_NOTE_BLOCK_BANJO.play(player, 1F, 1F);
                ChatUtil.sendMessage(player, LanguageResource.COMBAT_SWORD_MESSAGE_UN_EQUIPPING
                        .replace("%countdown%", String.valueOf(countdown)));
                break;
        }

        if (isCooldownMode()) {
            countdown--;
        }
    }

    public void start() {
        long initialDelay = isCooldownMode() ? 20L : 0L;
        this.runTaskTimerAsynchronously(plugin, initialDelay, 20L);
    }

    public void reset() {
        this.countdown = getInitialCountdown();
    }

    private boolean isCooldownMode() {
        return ConfigResource.PVP_ACTIVATION_MODE == ConfigResource.PvPActivationMode.COOLDOWN;
    }

    private int getInitialCountdown() {
        return isCooldownMode() ? ConfigResource.PVP_ACTIVATION_COOLDOWN_SECONDS : 0;
    }

    private void completeEquipping(Player player) {
        reset();

        XSound.ENTITY_ARMOR_STAND_PLACE.play(player, 1F, 1F);
        combatPlayer.onApplyEquipment(combatController);
        ChatUtil.sendMessage(player, LanguageResource.COMBAT_SWORD_MESSAGE_EQUIPPED);
    }

    private void completeUnEquipping(Player player) {
        reset();

        XSound.ENTITY_ARMOR_STAND_PLACE.play(player, 1F, 1F);
        combatPlayer.onApplyEquipment(combatController);
        ChatUtil.sendMessage(player, LanguageResource.COMBAT_SWORD_MESSAGE_UN_EQUIPPED);
    }
}