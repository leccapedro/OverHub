package studio.overmine.overhub.listeners;

import studio.overmine.overhub.models.resources.types.ConfigResource;
import studio.overmine.overhub.utilities.ChatUtil;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class PlayerListener implements Listener {

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		event.setJoinMessage(null);

		Player player = event.getPlayer();

		player.setHealth(20.0);
		player.setFoodLevel(20);
		player.setExp(0);
		player.setGameMode(GameMode.ADVENTURE);
		player.setWalkSpeed(0.5F);

		ConfigResource.WELCOME_MESSAGE
				.forEach(message -> ChatUtil.sendPlaceholderMessage(player, message
						.replace("%player%", player.getName())));
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		event.setCancelled(event.getPlayer().getGameMode() != GameMode.CREATIVE);
	}

	@EventHandler
	public void onPlayerItemPickup(PlayerPickupItemEvent event) {
		event.setCancelled(event.getPlayer().getGameMode() != GameMode.CREATIVE);
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		event.setCancelled(event.getWhoClicked().getGameMode() != GameMode.CREATIVE);
	}

	@EventHandler
	public void onPlayerDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerDamageByEntity(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player) {
			event.setCancelled(true);
		}
	}
}
