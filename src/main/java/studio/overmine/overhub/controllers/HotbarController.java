package studio.overmine.overhub.controllers;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import studio.overmine.overhub.models.hotbar.types.*;
import studio.overmine.overhub.models.resources.types.ConfigResource;
import studio.overmine.overhub.models.user.User;
import studio.overmine.overhub.models.user.VisibilityType;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import studio.overmine.overhub.OverHub;
import studio.overmine.overhub.models.hotbar.Hotbar;
import studio.overmine.overhub.utilities.FileConfig;
import studio.overmine.overhub.utilities.item.ItemBuilder;

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

    public void updateVisibilityHotbar(User user, Player player, VisibilityType visibilityType) {
        user.setVisibilityType(visibilityType);
        user.executeCurrentVisibility();

        userController.saveUser(user);

        Hotbar visibilityHotbar = getHotbar(visibilityType.getId());
        player.getInventory().setItem(visibilityHotbar.getItemSlot(), visibilityHotbar.getItemStack());
    }

    public Hotbar registerHotbar(Hotbar hotbar, ConfigurationSection section) {
        String hotbarName = hotbar.getName();

        hotbar.setEnabled(section.getBoolean(hotbarName + ".enabled"));
        hotbar.setItemStack(new ItemBuilder(section.getString(hotbarName + ".item.material"))
                .setDisplayName(section.getString(hotbarName + ".item.name"))
                .setLore(section.getStringList(hotbarName + ".item.lore"))
                .setSkullOwner(section.getString(hotbarName + ".item.head"))
                .setModelData(section.getInt(hotbarName + ".item.model-data"))
                .build());
        hotbar.setItemSlot(section.getInt(hotbarName + ".item.slot"));
        return hotbar;
    }

    public void registerHotbars(Hotbar... hotbars) {
        Arrays.stream(hotbars)
                .filter(Hotbar::isEnabled)
                .forEach(hotbar -> hotbarMap.put(hotbar.getName(), hotbar));
    }

    public void giveHotbar(Player player) {
        Inventory inventory = player.getInventory();
        inventory.clear();

        for (Hotbar hotbar : getHotbars()) {
            if (!hotbar.isUnique()) continue;
            inventory.setItem(hotbar.getItemSlot(), hotbar.getItemStack());
        }

        if (ConfigResource.HUB_SWORD_SYSTEM_ENABLED) {
            inventory.setItem(ConfigResource.HUB_SWORD_SYSTEM_SLOT, ConfigResource.HUB_SWORD_SYSTEM_SWORD);
        }

        User user = userController.getUser(player.getUniqueId());
        Hotbar visibilityHotbar = getHotbar(user.getVisibilityType().getId());

        if (visibilityHotbar != null) {
            inventory.setItem(visibilityHotbar.getItemSlot(), visibilityHotbar.getItemStack());
        }
    }

    public final void onReload(boolean reload) {
        hotbarMap.clear();

        ConfigurationSection defaultsSection = hotbarFile.getConfiguration().getConfigurationSection("defaults");
        if (defaultsSection == null) throw new IllegalStateException("No defaults section found in hotbar configuration.");

        registerHotbars(
                registerHotbar(new ServerSelectorHotbar("server-selector", plugin), defaultsSection),
                registerHotbar(new LobbySelectorHotbar("lobby-selector", plugin), defaultsSection),
                registerHotbar(new EnderButtHotbar("ender-butt"), defaultsSection),
                registerHotbar(new VisibilityHotbar("visibility-all", plugin, userController, this), defaultsSection),
                registerHotbar(new VisibilityHotbar("visibility-donator", plugin, userController, this), defaultsSection),
                registerHotbar(new VisibilityHotbar("visibility-staff", plugin, userController, this), defaultsSection),
                registerHotbar(new VisibilityHotbar("visibility-friend", plugin, userController, this), defaultsSection),
                registerHotbar(new VisibilityHotbar("visibility-none", plugin, userController, this), defaultsSection)
        );

        for (VisibilityType visibilityType : VisibilityType.values()) {
            Hotbar visibilityHotbar = getHotbar(visibilityType.getId());
            visibilityType.setEnabled(visibilityHotbar != null && visibilityHotbar.isEnabled());
        }

        ConfigurationSection customsSection = hotbarFile.getConfiguration().getConfigurationSection("customs");
        if (customsSection == null) throw new IllegalStateException("No customs section found in hotbar configuration.");

        customsSection.getKeys(false).forEach(customId -> {
            if (customsSection.getBoolean(customId + ".enabled")) {
                Hotbar hotbar = registerHotbar(new CustomHotbar(customId, customsSection.getStringList(customId + ".actions")), customsSection);
                hotbarMap.put(hotbar.getName(), hotbar);
            }
        });

        if (reload) Bukkit.getOnlinePlayers().forEach(this::giveHotbar);
    }
}
