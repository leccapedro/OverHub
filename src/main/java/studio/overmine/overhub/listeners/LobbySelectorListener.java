package studio.overmine.overhub.listeners;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import studio.overmine.overhub.OverHub;
import studio.overmine.overhub.controllers.LobbySelectorController;
import studio.overmine.overhub.models.resources.types.ConfigResource;
import studio.overmine.overhub.models.selector.lobby.LobbySelector;
import studio.overmine.overhub.utilities.ChatUtil;
import studio.overmine.overhub.utilities.ProxyUtil;
import studio.overmine.overhub.utilities.menu.Menu;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @author Risas
 * @date 08-03-2025
 */
public class LobbySelectorListener implements Listener {

    private final OverHub plugin;
    private final LobbySelectorController lobbySelectorController;

    public LobbySelectorListener(OverHub plugin) {
        this.plugin = plugin;
        this.lobbySelectorController = plugin.getLobbySelectorController();
    }

    @EventHandler
    public void onLobbySelectorInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        event.setCancelled(player.getGameMode() != GameMode.CREATIVE);

        ItemStack currentItem = event.getCurrentItem();
        if (currentItem == null || currentItem.getType() == Material.AIR) return;
        if (!Menu.hasMenu(player)) return;

        LobbySelector lobbySelector = lobbySelectorController.getLobbySelectorByItem(currentItem, player);
        if (lobbySelector == null) return;

        event.setCancelled(true);

        String lobby = lobbySelector.getLobby();

        if (lobby.equals(ConfigResource.SERVER_NAME)) {
            XSound.BLOCK_NOTE_BLOCK_BASS.play(player, 0.5f, 10.0f);
            ChatUtil.sendMessage(player, "&cYa estás en este lobby.");
            return;
        }

        if (!lobby.isEmpty()) ProxyUtil.sendServer(plugin, player, lobby);
    }
}
