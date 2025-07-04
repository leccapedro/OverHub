package studio.overmine.overhub.ui.buttons;

import studio.overmine.overhub.OverHub;
import studio.overmine.overhub.models.selector.server.ServerSelector;
import studio.overmine.overhub.ui.SubServerSelectorMenu;
import studio.overmine.overhub.utilities.ChatUtil;
import studio.overmine.overhub.utilities.item.ItemBuilder;
import studio.overmine.overhub.utilities.ProxyUtil;
import studio.overmine.overhub.utilities.menu.Button;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
public class ServerSelectorButton extends Button {

    private final OverHub plugin;
    private final ServerSelector serverSelector;

    @Override
    public ItemStack getItemStack(Player player) {
        ItemStack itemStack = serverSelector.getIcon().clone();
        return new ItemBuilder(itemStack)
                .setDisplayName(ChatUtil.placeholder(player, itemStack.getItemMeta().getDisplayName()))
                .setLore(ChatUtil.placeholder(player, itemStack.getItemMeta().getLore()))
                .build();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
        playNeutral(player);

        if (!serverSelector.getSubServerSelectors().isEmpty()) {
            SubServerSelectorMenu subServerSelectorMenu = new SubServerSelectorMenu(player, plugin, serverSelector);
            subServerSelectorMenu.open();
            return;
        }

        String server = serverSelector.getServer();
        if (!server.isEmpty()) ProxyUtil.sendServer(plugin, player, server);

        serverSelector.executeCommands(player);
    }
}
