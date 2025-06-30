package studio.overmine.overhub.controllers;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import studio.overmine.overhub.models.hotbar.types.VisibilityHotbar;
import studio.overmine.overhub.models.user.User;
import studio.overmine.overhub.models.user.VisibilityType;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import studio.overmine.overhub.OverHub;
import studio.overmine.overhub.models.hotbar.Hotbar;
import studio.overmine.overhub.models.hotbar.types.EnderButtHotbar;
import studio.overmine.overhub.models.hotbar.types.ServerSelectorHotbar;
import studio.overmine.overhub.utilities.FileConfig;
import studio.overmine.overhub.utilities.ItemBuilder;

public class HotbarController {

    private final OverHub plugin;
    private final FileConfig hotbarFile;
    private final UserController userController;
    private final Map<String, Hotbar> hotbarMap;

    public HotbarController(OverHub plugin) {
        this.plugin = plugin;
        this.hotbarFile = plugin.getFileConfig("hotbar");
        this.userController = plugin.getUserController();
        this.hotbarMap = new LinkedHashMap<>();
        this.onReload(false);
    }

    public Collection<Hotbar> getHotbars() {
        return hotbarMap.values();
    }

    public Hotbar getHotbar(String name) {
        return hotbarMap.get(name);
    }

    public Hotbar getHotbarByItem(ItemStack item) {
        return getHotbars().stream()
                .filter(hotbar -> hotbar.isSimilar(item))
                .findFirst()
                .orElse(null);
    }

    public void updateVisibilityHotbar(Player player, VisibilityType visibilityType) {
        User user = userController.getUser(player.getUniqueId());
        user.setVisibilityType(visibilityType);
        user.executeCurrentVisibility();

        userController.saveUser(user);

        Hotbar visibilityHotbar = getHotbar(user.getVisibilityType().getId());
        player.getInventory().setItem(visibilityHotbar.getItemSlot(), visibilityHotbar.getItemStack());
    }

    public Hotbar registerHotbar(Hotbar hotbar, ConfigurationSection section) {
        String hotbarName = hotbar.getName();

        hotbar.setItemStack(new ItemBuilder(section.getString(hotbarName + ".item.material"))
                .setDisplayName(section.getString(hotbarName + ".item.name"))
                .setLore(section.getStringList(hotbarName + ".item.description"))
                .setData(section.getInt(hotbarName + ".item.data"))
                .setSkullOwner(section.getString(hotbarName + ".item.head"))
                .build());
        hotbar.setItemSlot(section.getInt(hotbarName + ".item.slot"));
        return hotbar;
    }

    public void registerHotbars(Hotbar... hotbars) {
        for (Hotbar hotbar : hotbars) {
            hotbarMap.put(hotbar.getName(), hotbar);
        }
    }

    public void giveHotbar(Player player) {
        Inventory inventory = player.getInventory();
        inventory.clear();

        for (Hotbar hotbar : getHotbars()) {
            if (!hotbar.isUnique()) continue;
            inventory.setItem(hotbar.getItemSlot(), hotbar.getItemStack());
        }

        User user = userController.getUser(player.getUniqueId());
        Hotbar visibilityHotbar = getHotbar(user.getVisibilityType().getId());
        inventory.setItem(visibilityHotbar.getItemSlot(), visibilityHotbar.getItemStack());
    }

    public final void onReload(boolean reload) {
        if (reload) hotbarMap.clear();

        ConfigurationSection section = hotbarFile.getConfiguration();

        registerHotbars(
                registerHotbar(new ServerSelectorHotbar("server-selector", plugin), section),
                registerHotbar(new EnderButtHotbar("ender-butt"), section),
                registerHotbar(new VisibilityHotbar("visibility-all", plugin,this, VisibilityType.DONATOR), section),
                registerHotbar(new VisibilityHotbar("visibility-donator", plugin, this, VisibilityType.STAFF), section),
                registerHotbar(new VisibilityHotbar("visibility-staff", plugin,this, VisibilityType.FRIEND), section),
                registerHotbar(new VisibilityHotbar("visibility-friend", plugin,this, VisibilityType.NONE), section),
                registerHotbar(new VisibilityHotbar("visibility-none", plugin,this, VisibilityType.ALL), section)
        );

        if (reload) Bukkit.getOnlinePlayers().forEach(this::giveHotbar);
    }
}
