
package studio.overmine.overhub.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import studio.overmine.overhub.OverHub;
import studio.overmine.overhub.controllers.CombatController;
import studio.overmine.overhub.controllers.HotbarController;
import studio.overmine.overhub.controllers.SpawnController;
import studio.overmine.overhub.controllers.UserController;
import studio.overmine.overhub.models.combat.CombatPlayer;
import studio.overmine.overhub.models.combat.CombatStatus;
import studio.overmine.overhub.models.resources.types.ConfigResource;
import studio.overmine.overhub.models.resources.types.LanguageResource;
import studio.overmine.overhub.models.user.User;
import studio.overmine.overhub.models.user.types.PvpState;
import studio.overmine.overhub.utilities.ChatUtil;

/**
 * @author Risas
 * @date 05-07-2025
 * @discord https://risas.me/discord
 */
public class CombatListener implements Listener {

    private final OverHub plugin;
    private final CombatController combatController;
    private final UserController userController;

    public CombatListener(OverHub plugin, CombatController combatController) {
        this.plugin = plugin;
        this.combatController = combatController;
        this.userController = plugin.getUserController();
    }

    @EventHandler
    public void onCombatSword(PlayerItemHeldEvent event) {
        if (!ConfigResource.PVP_MODE_ENABLED) return;

        Player player = event.getPlayer();
        int newSlot = event.getNewSlot();
        int previousSlot = event.getPreviousSlot();
        int swordSlot = ConfigResource.PVP_SWORD_SLOT;

        boolean isSelectingSwordSlot = (newSlot == swordSlot);
        boolean isDeselectingSwordSlot = (previousSlot == swordSlot);

        if (!isSelectingSwordSlot && !isDeselectingSwordSlot) {
            return;
        }

        CombatPlayer combatPlayer = combatController.getCombatPlayer(player);

        if (isSelectingSwordSlot) {
            if (combatPlayer != null && combatPlayer.isPvP()) {
                combatPlayer.stopCombatTask();
                return;
            }

            if (combatPlayer != null && combatPlayer.getStatus() == CombatStatus.EQUIPPING) {
                return;
            }
        }

        if (isDeselectingSwordSlot && combatPlayer != null) {
            if (combatPlayer.getStatus() == CombatStatus.EQUIPPING) {
                combatPlayer.stopCombatTask();
                combatController.removeCombatPlayer(player);

                User user = userController.getUser(player.getUniqueId());
                if (user != null) {
                    user.setPvpState(PvpState.INACTIVE);
                    user.setPvpEnabled(false);
                }
                return;
            }

            if (combatPlayer.isPvP()) {
                if (combatPlayer.isInCombat()) {
                    return;
                }
                return;
            }
        }
    }

    @EventHandler
    public void onSwordInteract(PlayerInteractEvent event) {
        if (!ConfigResource.PVP_MODE_ENABLED) return;

        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return;

        if (ConfigResource.PVP_SWORD_ITEM == null) return;

        if (event.getItem() == null || !event.getItem().isSimilar(ConfigResource.PVP_SWORD_ITEM)) return;

        Player player = event.getPlayer();
        CombatPlayer combatPlayer = combatController.getCombatPlayer(player);

        event.setCancelled(true);

        if (combatPlayer == null) {
            combatController.addCombatPlayer(player);
            return;
        }

        if (combatPlayer.isPvP()) {
            combatPlayer.stopCombatTask();
            return;
        }

        if (combatPlayer.getStatus() == CombatStatus.EQUIPPING) {
            return;
        }
    }

    @EventHandler
    public void onExitItemInteract(PlayerInteractEvent event) {
        if (!ConfigResource.PVP_MODE_ENABLED) return;

        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return;

        if (ConfigResource.PVP_EXIT_ITEM == null) return;

        if (event.getItem() == null || !event.getItem().isSimilar(ConfigResource.PVP_EXIT_ITEM)) return;

        Player player = event.getPlayer();
        CombatPlayer combatPlayer = combatController.getCombatPlayer(player);
        if (combatPlayer == null || !combatPlayer.isPvP()) return;

        event.setCancelled(true);

        if (combatPlayer.isInCombat()) {
            ChatUtil.sendMessage(player, LanguageResource.COMBAT_PVP_EXIT_IN_COMBAT
                    .replace("%time%", String.valueOf(combatPlayer.getCombatTimeRemainingSeconds())));
            return;
        }

        if (!combatPlayer.isCombatTaskRunning()) {
            combatPlayer.startCombatTask(plugin, combatController);
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

            recordLastHit(player, damager);
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
        Player player = event.getPlayer();
        CombatPlayer combatPlayer = combatController.getCombatPlayer(player);
        if (combatPlayer != null) {
            combatController.removeCombatPlayer(player);
        }

        User user = userController.getUser(player.getUniqueId());
        if (user != null && user.isPvpEnabled()) {
            user.setPvpEnabled(false);
            user.setPvpState(PvpState.INACTIVE);
            userController.saveUser(user);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        CombatPlayer victimCombatPlayer = combatController.getCombatPlayer(victim);
        boolean victimInPvp = victimCombatPlayer != null && victimCombatPlayer.isPvP();
        if (victimCombatPlayer != null) {
            victimCombatPlayer.resetCombatState(true);
        }

        User victimUser = userController.getUser(victim.getUniqueId());
        if (victimUser != null && victimInPvp) {
            victimUser.resetPvpKillStreak();
            victimUser.clearLastHitBy();
            userController.saveUser(victimUser);
        }

        Player killer = victim.getKiller();
        if (killer != null && victimInPvp) {
            CombatPlayer killerCombatPlayer = combatController.getCombatPlayer(killer);
            if (killerCombatPlayer != null && killerCombatPlayer.isPvP()) {
                User killerUser = userController.getUser(killer.getUniqueId());
                if (killerUser != null) {
                    killerUser.incrementPvpKills();
                    killerUser.incrementPvpKillStreak();
                    userController.saveUser(killerUser);
                    
                    String killMessage = LanguageResource.COMBAT_PVP_KILL
                            .replace("{victim}", victim.getName())
                            .replace("{kills}", String.valueOf(killerUser.getPvpKills()))
                            .replace("{streak}", String.valueOf(killerUser.getPvpKillStreak()));
                    ChatUtil.sendMessage(killer, killMessage);
                    
                    event.setDeathMessage(null);
                }
            }
        }
        
        if (victimInPvp && killer != null) {
            String deathMessage = LanguageResource.COMBAT_PVP_DEATH
                    .replace("{killer}", killer.getName());
            ChatUtil.sendMessage(victim, deathMessage);
        }
        
        new BukkitRunnable() {
            @Override
            public void run() {
                if (victim.isOnline()) {
                    victim.spigot().respawn();
                }
            }
        }.runTask(plugin);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        User user = userController.getUser(player.getUniqueId());
        
        if (user != null && user.isPvpEnabled()) {
            CombatPlayer combatPlayer = combatController.getCombatPlayer(player);
            if (combatPlayer == null || !combatPlayer.isPvP()) {
                user.setPvpEnabled(false);
                user.setPvpState(PvpState.INACTIVE);
                userController.saveUser(user);
                return;
            }
            
            SpawnController spawnController = plugin.getSpawnController();
            if (spawnController != null) {
                Location combatSpawn = spawnController.getCombatSpawn(player);
                if (combatSpawn != null) {
                    event.setRespawnLocation(combatSpawn);
                    
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (player.isOnline()) {
                                player.setVelocity(new Vector(0, 0, 0));
                                player.setFallDistance(0);
                                player.setNoDamageTicks(40);
                                
                                HotbarController hotbarController = plugin.getHotbarController();
                                if (hotbarController != null) {
                                    hotbarController.resetPvpInventory(player);
                                }
                            }
                        }
                    }.runTask(plugin);
                }
            }
        }
    }

    private void recordLastHit(Player victim, Player damager) {
        User victimUser = userController.getUser(victim.getUniqueId());
        if (victimUser == null) {
            return;
        }

        victimUser.setLastHitBy(damager.getName());
    }
}