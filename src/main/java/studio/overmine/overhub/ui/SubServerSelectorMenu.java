package studio.overmine.overhub.ui;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import studio.overmine.overhub.OverHub;
import studio.overmine.overhub.models.selector.server.ServerSelector;
import studio.overmine.overhub.models.selector.server.SubServerSelector;
import studio.overmine.overhub.ui.buttons.SubServerSelectorButton;
import studio.overmine.overhub.utilities.ChatUtil;
import studio.overmine.overhub.utilities.menu.Button;
import studio.overmine.overhub.utilities.menu.Menu;
import studio.overmine.overhub.utilities.menu.decoration.Decoration;

public class SubServerSelectorMenu extends Menu {

    private final OverHub plugin;
    private final ServerSelector serverSelector;

    public SubServerSelectorMenu(Player player, OverHub plugin, ServerSelector serverSelector) {
        super(plugin, player,
                ChatUtil.translate(serverSelector.getMenuTitle()
                                .replace("%server%", serverSelector.getName())),
                serverSelector.getMenuRows() * 9, true
        );
        this.plugin = plugin;
        this.serverSelector = serverSelector;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        HashMap<Integer, Button> buttonMap = new HashMap<>();

        for (Decoration decoration : serverSelector.getMenuDecorations()) {
            buttonMap.put(decoration.getSlot(), Button.getButton(decoration.getItemStack(player)));
        }

        for (SubServerSelector subServerSelector : serverSelector.getSubServerSelectors()) {
            buttonMap.put(subServerSelector.getIconSlot(), new SubServerSelectorButton(plugin, subServerSelector));
        }

        return buttonMap;
    }

    @Override
    public boolean isLobbies() {
        return true;
    }
}
