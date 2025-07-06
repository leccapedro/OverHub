package studio.overmine.overhub.listeners;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import studio.overmine.overhub.OverHub;
import studio.overmine.overhub.controllers.CombatController;
import studio.overmine.overhub.models.resources.types.CombatSwordResource;
import studio.overmine.overhub.models.resources.types.LanguageResource;
import studio.overmine.overhub.tasks.CombatTask;
import studio.overmine.overhub.utilities.ChatUtil;

import java.util.UUID;

/**
 * @author Risas
 * @date 05-07-2025
 * @discord https://risas.me/discord
 */
public class CombatListener implements Listener {
    private final OverHub plugin;
    private final CombatController combatController;

    public CombatListener(OverHub plugin) {
        this.plugin = plugin;
        this.combatController = plugin.getCombatController();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        ItemStack item = combatController.buildItem(CombatSwordResource.ITEM_SWORD);
        player.getInventory().setItem(2, item);
    }

    @EventHandler
    public void onHotbarHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack newItem = player.getInventory().getItem(event.getNewSlot());
        UUID playerId = player.getUniqueId();
        FileConfiguration config = plugin.getConfig();

        if (combatController.getEquipTasks().containsKey(playerId)) {
            combatController.getEquipTasks().get(playerId).cancel();
            combatController.getEquipTasks().remove(playerId);
            ChatUtil.sendMessage(player, LanguageResource.COMBAT_SWORD_TASK_CANCEL);
        }

        ConfigurationSection swordSection = config.getConfigurationSection("sword");
        ItemStack swordItem = combatController.buildItem(swordSection);

        if (newItem != null && newItem == swordItem) {
            combatController.getPlayersPvP().put(playerId, true);
            ChatUtil.sendMessage(player, LanguageResource.COMBAT_SWORD_TASK_START);

            CombatTask task = new CombatTask(player, combatController::equipPlayer);
            BukkitTask bukkitTask = task.runTaskTimer(plugin, 0L, 20L);
            combatController.getEquipTasks().put(playerId, bukkitTask);
        } else {
            combatController.getPlayersPvP().put(playerId, false);
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) {
            return;
        }

        Player victim = (Player) event.getEntity();
        Player attacker = (Player) event.getDamager();
        UUID victimId = victim.getUniqueId();
        UUID attackerId = attacker.getUniqueId();

        boolean victimInPvP = combatController.getPlayersPvP().getOrDefault(victimId, false);
        boolean attackerInPvP = combatController.getPlayersPvP().getOrDefault(attackerId, false);

        if (!victimInPvP || !attackerInPvP) {
            event.setCancelled(true);
            ChatUtil.sendMessage(victim, LanguageResource.COMBAT_SWORD_DISABLED);
        }
    }
}
