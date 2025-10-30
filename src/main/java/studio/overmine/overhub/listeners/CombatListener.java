package studio.overmine.overhub.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import studio.overmine.overhub.OverHub;
import studio.overmine.overhub.controllers.CombatController;
import studio.overmine.overhub.models.combat.CombatPlayer;
import studio.overmine.overhub.models.combat.CombatStatus;
import studio.overmine.overhub.models.resources.types.ConfigResource;
import studio.overmine.overhub.models.resources.types.LanguageResource;
import studio.overmine.overhub.utilities.ChatUtil;

/**
 * @author Risas
 * @date 05-07-2025
 * @discord https://risas.me/discord
 */
public class CombatListener implements Listener {

    private final OverHub plugin;
    private final CombatController combatController;

    public CombatListener(OverHub plugin, CombatController combatController) {
        this.plugin = plugin;
        this.combatController = combatController;
    }

    @EventHandler
    public void onCombatSword(PlayerItemHeldEvent event) {
        if (!ConfigResource.PVP_MODE_ENABLED) return;

        Player player = event.getPlayer();
        ItemStack itemStack = player.getInventory().getItem(event.getNewSlot());

        if (itemStack != null && itemStack.equals(ConfigResource.PVP_SWORD_ITEM)) {
            CombatPlayer combatPlayer = combatController.getCombatPlayer(player);

            if (combatPlayer != null && combatPlayer.isPvP()) {
                combatPlayer.stopCombatTask();
                return;
            }

            combatController.addCombatPlayer(player);
            return;
        }

        CombatPlayer combatPlayer = combatController.getCombatPlayer(player);
        if (combatPlayer == null) return;

        if (combatPlayer.getStatus() == CombatStatus.EQUIPPING) {
            combatPlayer.stopCombatTask();
            combatController.removeCombatPlayer(player);
        }
        else if (combatPlayer.isPvP()) {
            if (combatPlayer.isInCombat()) {
                ChatUtil.sendMessage(player, LanguageResource.COMBAT_SWORD_MESSAGE_IN_COMBAT
                        .replace("%time%", String.valueOf(combatPlayer.getCombatTimeRemainingSeconds())));
                return;
            }

            if (!combatPlayer.isCombatTaskRunning()) {
                combatPlayer.startCombatTask(plugin, combatController);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onCombatDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) return;

        event.setCancelled(true);

        Player player = (Player) event.getEntity();
        CombatPlayer combatPlayer = combatController.getCombatPlayer(player);
        if (combatPlayer == null) return;

        Player damager = (Player) event.getDamager();
        CombatPlayer damagerCombatPlayer = combatController.getCombatPlayer(damager);
        if (damagerCombatPlayer == null) return;

        if (combatPlayer.isPvP() && damagerCombatPlayer.isPvP()) {
            combatPlayer.stopCombatTask();
            damagerCombatPlayer.stopCombatTask();

            combatPlayer.startOrRefreshCombat(plugin, combatController);
            damagerCombatPlayer.startOrRefreshCombat(plugin, combatController);

            event.setCancelled(false);
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        CombatPlayer combatPlayer = combatController.getCombatPlayer((Player) event.getEntity());
        if (combatPlayer != null && combatPlayer.isPvP()) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onCombatQuit(PlayerQuitEvent event) {
        CombatPlayer combatPlayer = combatController.getCombatPlayer(event.getPlayer());
        if (combatPlayer == null) return;

        combatController.removeCombatPlayer(event.getPlayer());
    }
}