package studio.overmine.overhub.tasks;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import studio.overmine.overhub.OverHub;
import studio.overmine.overhub.controllers.CombatController;
import studio.overmine.overhub.models.combat.CombatPlayer;
import studio.overmine.overhub.models.resources.types.ConfigResource;
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
        this.countdown = ConfigResource.HUB_SWORD_SYSTEM_DELAY;
    }

    @Override
    public void run() {
        Player player = combatPlayer.getPlayer();

        switch (combatPlayer.getStatus()) {
            case EQUIPPING:
                if (countdown <= 0) {
                    reset();

                    combatPlayer.onApplyEquipment(combatController);
                    ChatUtil.sendMessage(player, "&4&lLOBBY &8» &fTu modo pvp se ha &aactivado&f.");
                    return;
                }

                ChatUtil.sendMessage(player, "&4&lLOBBY &8» &fTu modo pvp se activará en &e" + countdown + " &fsegundos.");
                break;
            case UN_EQUIPPING:
                if (countdown <= 0) {
                    reset();

                    combatPlayer.onApplyEquipment(combatController);
                    ChatUtil.sendMessage(player, "&4&lLOBBY &8» &fTu modo pvp se ha &cdesactivado&f.");
                    return;
                }

                ChatUtil.sendMessage(player, "&4&lLOBBY &8» &fTu modo pvp se desactivará en &e" + countdown + " &fsegundos.");
                break;
        }

        countdown--;
    }

    public void start() {
        this.runTaskTimerAsynchronously(plugin, 20L, 20L);
    }

    public void reset() {
        this.countdown = ConfigResource.HUB_SWORD_SYSTEM_DELAY;
    }
}
