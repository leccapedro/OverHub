package studio.overmine.overhub.listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.player.*;
import studio.overmine.overhub.OverHub;
import studio.overmine.overhub.controllers.CombatController;
import studio.overmine.overhub.models.combat.CombatPlayer;
import studio.overmine.overhub.models.resources.types.ConfigResource;
import studio.overmine.overhub.models.resources.types.LanguageResource;
import studio.overmine.overhub.utilities.ChatUtil;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class PlayerListener implements Listener {

    private final OverHub plugin;

    public PlayerListener(OverHub plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (!ConfigResource.CHAT_SYSTEM_ENABLED) return;

        event.setCancelled(true);

        Player player = event.getPlayer();
        String message = player.hasPermission("overhub.chat.color") ?
                ChatUtil.translate(event.getMessage()) :
                ChatColor.stripColor(event.getMessage());

        event.getRecipients().forEach(recipient ->
                recipient.sendMessage(ChatUtil.placeholder(player, ConfigResource.CHAT_SYSTEM_FORMAT
                                .replace("%player%", player.getName()))
                        .replace("%message%", message)));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        event.setJoinMessage(LanguageResource.SERVER_MESSAGE_ON_JOIN.isEmpty() ? null :
                ChatUtil.placeholder(player, LanguageResource.SERVER_MESSAGE_ON_JOIN
                        .replace("%player%", player.getName())));

        player.setHealth(20.0);
        player.setFoodLevel(20);
        player.setExp(0);
        player.setGameMode(GameMode.ADVENTURE);

        ConfigResource.WELCOME_MESSAGE
                .forEach(message -> ChatUtil.sendPlaceholderMessage(player, message
                        .replace("%player%", player.getName())));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        event.setQuitMessage(LanguageResource.SERVER_MESSAGE_ON_QUIT.isEmpty() ? null :
                ChatUtil.placeholder(player, LanguageResource.SERVER_MESSAGE_ON_QUIT
                        .replace("%player%", player.getName())));
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        CombatController combatController = plugin.getCombatController();
        if (combatController != null) {
            CombatPlayer combatPlayer = combatController.getCombatPlayer(player);
            if (combatPlayer != null && combatPlayer.isPvP()) {
                return;
            }
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerItemPickup(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();

        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        CombatController combatController = plugin.getCombatController();
        if (combatController != null) {
            CombatPlayer combatPlayer = combatController.getCombatPlayer(player);
            if (combatPlayer != null && combatPlayer.isPvP()) {
                return;
            }
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();

        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        CombatController combatController = plugin.getCombatController();
        if (combatController != null) {
            CombatPlayer combatPlayer = combatController.getCombatPlayer(player);
            if (combatPlayer != null && combatPlayer.isPvP()) {
                return;
            }
        }

        event.setCancelled(true);
    }
}