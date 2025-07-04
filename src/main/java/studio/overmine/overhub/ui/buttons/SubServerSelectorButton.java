package studio.overmine.overhub.ui.buttons;

import studio.overmine.overhub.OverHub;
import studio.overmine.overhub.models.selector.server.SubServerSelector;
import studio.overmine.overhub.utilities.ChatUtil;
import studio.overmine.overhub.utilities.item.ItemBuilder;
import studio.overmine.overhub.utilities.ProxyUtil;
import studio.overmine.overhub.utilities.menu.Button;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
public class SubServerSelectorButton extends Button {

    private final OverHub plugin;
    private final SubServerSelector subServerSelector;

    @Override
    public ItemStack getItemStack(Player player) {
        ItemStack itemStack = subServerSelector.getIcon().clone();
        return new ItemBuilder(itemStack)
                .setDisplayName(ChatUtil.placeholder(player, itemStack.getItemMeta().getDisplayName()))
                .setLore(ChatUtil.placeholder(player, itemStack.getItemMeta().getLore()))
                .build();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
        playNeutral(player);

        String subServer = subServerSelector.getSubServer();
        if (!subServer.isEmpty()) ProxyUtil.sendServer(plugin, player, subServer);

        subServerSelector.executeCommands(player);
    }
}
