package studio.overmine.overhub.models.hotbar.types;

import studio.overmine.overhub.OverHub;
import studio.overmine.overhub.models.hotbar.Hotbar;
import studio.overmine.overhub.ui.ServerSelectorMenu;
import org.bukkit.entity.Player;

public class ServerSelectorHotbar extends Hotbar {

    private final OverHub plugin;

    public ServerSelectorHotbar(String name, OverHub plugin) {
        super(name, true);
        this.plugin = plugin;
    }

    @Override
    public void onAction(Player player) {
        ServerSelectorMenu serverSelectorMenu = new ServerSelectorMenu(player, plugin);
        serverSelectorMenu.open();
    }
}
