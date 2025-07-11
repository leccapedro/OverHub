package studio.overmine.overhub.models.hotbar.types;

import org.bukkit.entity.Player;
import studio.overmine.overhub.OverHub;
import studio.overmine.overhub.models.hotbar.Hotbar;
import studio.overmine.overhub.ui.LobbySelectorMenu;

public class LobbySelectorHotbar extends Hotbar {

    private final OverHub plugin;

    public LobbySelectorHotbar(String name, OverHub plugin) {
        super(name, true);
        this.plugin = plugin;
    }

    @Override
    public void onAction(Player player) {
        LobbySelectorMenu lobbySelectorMenu = new LobbySelectorMenu(player, plugin);
        lobbySelectorMenu.open();
    }
}
