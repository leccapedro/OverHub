package studio.overmine.overhub.utilities.menu;

import java.util.Map;
import java.util.UUID;

import studio.overmine.overhub.OverHub;
import studio.overmine.overhub.models.resources.types.SelectorResource;
import studio.overmine.overhub.models.selector.lobby.LobbySelector;
import studio.overmine.overhub.utilities.menu.decoration.Decoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.google.common.collect.Maps;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public abstract class Menu {

    protected static Map<UUID, Menu> menus = Maps.newHashMap();
    protected Map<Integer, Button> buttons = Maps.newHashMap();

    protected OverHub plugin;
    protected Player player;
    protected Inventory inventory;
    protected String title;
    protected boolean allowInteract, updateAfterClick, lobbies;
    protected int size;

    public Menu(OverHub plugin, Player player, String title, int size, boolean lobbies) {
        this.plugin = plugin;
        this.player = player;
        this.inventory = Bukkit.createInventory(null, size, title);
        this.allowInteract = false;
        this.lobbies = lobbies;
        this.title = title;
        this.size = size;
    }

    public void open() {
        this.buttons = this.getButtons(player);

        for (Map.Entry<Integer, Button> entry : buttons.entrySet()) {
            inventory.setItem(entry.getKey(), entry.getValue().getItemStack(player));
        }

        if (lobbies) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                Inventory playerInventory = player.getInventory();
                playerInventory.clear();

                for (Decoration decoration : SelectorResource.LOBBY_SELECTOR_MENU_DECORATIONS) {
                    playerInventory.setItem(decoration.getSlot(), decoration.getItemStack(player));
                }

                for (LobbySelector lobbySelector : plugin.getLobbySelectorController().getLobbySelectors()) {
                    playerInventory.setItem(lobbySelector.getIconSlot(), lobbySelector.getDisplayIcon(player));
                }
            }, 1L);
        }

        player.openInventory(inventory);
        player.updateInventory();

        menus.put(player.getUniqueId(), this);
    }

    public void close(Player player) {
        plugin.getHotbarController().giveHotbar(player);
        menus.remove(player.getUniqueId());
    }

    public int size(Map<Integer, Button> buttons) {
        int highest = 0;

        for (int buttonValue : buttons.keySet()) {
            if (buttonValue > highest) {
                highest = buttonValue;
            }
        }

        return (int) (Math.ceil((highest + 1) / 9D) * 9D);
    }

    public abstract Map<Integer, Button> getButtons(Player player);

    public static Menu getMenu(Player player) {
        return menus.get(player.getUniqueId());
    }

    public static boolean hasMenu(Player player) {
        return menus.containsKey(player.getUniqueId());
    }
}