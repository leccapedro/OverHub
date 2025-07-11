package studio.overmine.overhub.ui.buttons;

import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import studio.overmine.overhub.OverHub;
import studio.overmine.overhub.models.selector.lobby.LobbySelector;
import studio.overmine.overhub.utilities.ProxyUtil;
import studio.overmine.overhub.utilities.menu.Button;

@AllArgsConstructor
public class LobbySelectorButton extends Button {

    private final OverHub plugin;
    private final LobbySelector lobbySelector;

    @Override
    public ItemStack getItemStack(Player player) {
        return lobbySelector.getDisplayIcon(player);
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
        playNeutral(player);

        String lobby = lobbySelector.getLobby();
        if (!lobby.isEmpty()) ProxyUtil.sendServer(plugin, player, lobby);
    }
}
