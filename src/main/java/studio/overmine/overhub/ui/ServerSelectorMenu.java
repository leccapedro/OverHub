package studio.overmine.overhub.ui;

import java.util.HashMap;
import java.util.Map;

import studio.overmine.overhub.utilities.menu.decoration.Decoration;
import org.bukkit.entity.Player;

import studio.overmine.overhub.OverHub;
import studio.overmine.overhub.controllers.ServerSelectorController;
import studio.overmine.overhub.models.resources.types.SelectorResource;
import studio.overmine.overhub.models.selector.server.ServerSelector;
import studio.overmine.overhub.ui.buttons.ServerSelectorButton;
import studio.overmine.overhub.utilities.ChatUtil;
import studio.overmine.overhub.utilities.menu.Button;
import studio.overmine.overhub.utilities.menu.Menu;

public class ServerSelectorMenu extends Menu {

    private final OverHub plugin;
    private final ServerSelectorController serverSelectorController;

    public ServerSelectorMenu(Player player, OverHub plugin) {
        super(plugin, player,
                ChatUtil.translate(SelectorResource.SERVER_SELECTOR_MENU_TITLE),
                SelectorResource.SERVER_SELECTOR_MENU_ROWS * 9, true
        );
        this.plugin = plugin;
        this.serverSelectorController = plugin.getServerSelectorController();
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        HashMap<Integer, Button> buttonMap = new HashMap<>();

        for (Decoration decoration : SelectorResource.SERVER_SELECTOR_MENU_DECORATIONS) {
            buttonMap.put(decoration.getSlot(), Button.getButton(decoration.getItemStack(player)));
        }

        for (ServerSelector serverSelector : serverSelectorController.getServerSelectors()) {
            buttonMap.put(serverSelector.getIconSlot(), new ServerSelectorButton(plugin, serverSelector));
        }

        return buttonMap;
    }
}
