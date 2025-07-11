package studio.overmine.overhub.ui;

import org.bukkit.entity.Player;
import studio.overmine.overhub.OverHub;
import studio.overmine.overhub.controllers.LobbySelectorController;
import studio.overmine.overhub.models.resources.types.SelectorResource;
import studio.overmine.overhub.models.selector.lobby.LobbySelector;
import studio.overmine.overhub.ui.buttons.LobbySelectorButton;
import studio.overmine.overhub.utilities.ChatUtil;
import studio.overmine.overhub.utilities.menu.Button;
import studio.overmine.overhub.utilities.menu.Menu;
import studio.overmine.overhub.utilities.menu.decoration.DecorationUtil;

import java.util.HashMap;
import java.util.Map;

public class LobbySelectorMenu extends Menu {

    private final OverHub plugin;
    private final LobbySelectorController lobbySelectorController;

    public LobbySelectorMenu(Player player, OverHub plugin) {
        super(
                plugin,
                player,
                ChatUtil.translate(SelectorResource.LOBBY_SELECTOR_MENU_TITLE),
                SelectorResource.LOBBY_SELECTOR_MENU_ROWS * 9,
                false
        );
        this.plugin = plugin;
        this.lobbySelectorController = plugin.getLobbySelectorController();
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        HashMap<Integer, Button> buttons = new HashMap<>();

        DecorationUtil.loadDecorations(buttons, SelectorResource.LOBBY_SELECTOR_MENU_DECORATIONS);

        for (LobbySelector lobbySelector : lobbySelectorController.getLobbySelectors()) {
            buttons.put(lobbySelector.getIconSlot(), new LobbySelectorButton(plugin, lobbySelector));
        }

        return buttons;
    }
}
