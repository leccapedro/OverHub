package studio.overmine.overhub.controllers;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import studio.overmine.overhub.models.combat.CombatPlayer;
import studio.overmine.overhub.models.hotbar.types.*;
import studio.overmine.overhub.models.resources.types.ConfigResource;
import studio.overmine.overhub.models.user.User;
import studio.overmine.overhub.models.user.VisibilityType;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import studio.overmine.overhub.OverHub;
import studio.overmine.overhub.models.hotbar.Hotbar;
import studio.overmine.overhub.utilities.FileConfig;
import studio.overmine.overhub.utilities.item.ItemBuilder;

public class HotbarController {

    private final OverHub plugin;
    private final FileConfig hotbarFile;
    private final UserController userController;
    private final Map<String, Hotbar> hotbarMap;
    private final ConcurrentMap<UUID, ItemStack[]> lobbyInventorySnapshots;
    private final ConcurrentMap<UUID, ItemStack> lobbyOffhandSnapshots;

    public HotbarController(OverHub plugin) {
        this.plugin = plugin;
        this.hotbarFile = plugin.getFileConfig("hotbar");
        this.userController = plugin.getUserController();
        this.hotbarMap = new LinkedHashMap<>();
        this.lobbyInventorySnapshots = new ConcurrentHashMap<>();
        this.lobbyOffhandSnapshots = new ConcurrentHashMap<>();
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
        player.getInventory().setItem(visibilityHotbar.getItemSlot(), cloneItem(visibilityHotbar.getItemStack()));
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
        CombatPlayer combatPlayer = plugin.getCombatController().getCombatPlayer(player);
        if (combatPlayer != null && (combatPlayer.isPvP() || combatPlayer.isInCombat())) {
            return;
        }
        applyLobbyHotbar(player, true);
    }

    public boolean applyPvpHotbar(Player player) {
        UUID uuid = player.getUniqueId();
        PlayerInventory inventory = player.getInventory();
        int storageSize = inventory.getStorageContents().length;

        lobbyInventorySnapshots.put(uuid, cloneStorageContents(inventory.getStorageContents()));
        lobbyOffhandSnapshots.put(uuid, cloneItem(inventory.getItemInOffHand()));

        inventory.setStorageContents(new ItemStack[storageSize]);
        inventory.setItemInOffHand(null);

        User user = userController.getUser(uuid);
        boolean hasLayout = user != null && user.hasPvpHotbar();
        if (user != null) {
            inventory.setStorageContents(user.getPvpHotbarClone());
        }

        if (ConfigResource.PVP_SWORD_ITEM != null) {
            inventory.setItem(ConfigResource.PVP_SWORD_SLOT, cloneItem(ConfigResource.PVP_SWORD_ITEM));
        }

        if (ConfigResource.PVP_EXIT_ITEM != null) {
            inventory.setItem(ConfigResource.PVP_EXIT_ITEM_SLOT, cloneItem(ConfigResource.PVP_EXIT_ITEM));
        }

        return hasLayout;
    }

    public void restoreLobbyHotbar(Player player) {
        UUID uuid = player.getUniqueId();
        PlayerInventory inventory = player.getInventory();
        int storageSize = inventory.getStorageContents().length;

        ItemStack[] storedContents = lobbyInventorySnapshots.remove(uuid);
        if (storedContents != null) {
            inventory.setStorageContents(cloneStorageContents(storedContents));
        } else {
            inventory.setStorageContents(new ItemStack[storageSize]);
        }

        ItemStack offhandItem = lobbyOffhandSnapshots.remove(uuid);
        inventory.setItemInOffHand(cloneItem(offhandItem));

        applyLobbyHotbar(player, false);
    }

    public void discardLobbySnapshot(UUID uuid) {
        lobbyInventorySnapshots.remove(uuid);
        lobbyOffhandSnapshots.remove(uuid);
    }

    private void applyLobbyHotbar(Player player, boolean clearInventory) {
        PlayerInventory inventory = player.getInventory();
        if (clearInventory) {
            inventory.clear();
        }

        for (Hotbar hotbar : getHotbars()) {
            if (!hotbar.isUnique()) continue;
            ItemStack itemStack = hotbar.getItemStack();
            if (itemStack != null) {
                inventory.setItem(hotbar.getItemSlot(), cloneItem(itemStack));
            }
        }

        if (ConfigResource.PVP_MODE_ENABLED && ConfigResource.PVP_SWORD_ITEM != null) {
            inventory.setItem(ConfigResource.PVP_SWORD_SLOT, cloneItem(ConfigResource.PVP_SWORD_ITEM));
        }

        User user = userController.getUser(player.getUniqueId());
        if (user == null) {
            return;
        }

        Hotbar visibilityHotbar = getHotbar(user.getVisibilityType().getId());
        if (visibilityHotbar != null && visibilityHotbar.getItemStack() != null) {
            inventory.setItem(visibilityHotbar.getItemSlot(), cloneItem(visibilityHotbar.getItemStack()));
        }
    }

    private ItemStack[] cloneStorageContents(ItemStack[] contents) {
        ItemStack[] clone = new ItemStack[contents.length];
        for (int i = 0; i < contents.length; i++) {
            clone[i] = cloneItem(contents[i]);
        }
        return clone;
    }

    private ItemStack cloneItem(ItemStack itemStack) {
        return itemStack == null ? null : itemStack.clone();
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
                Hotbar hotbar = registerHotbar(new CustomHotbar(customId, customsSection.getStringList(customId + ".actions")),
                        customsSection);
                hotbarMap.put(hotbar.getName(), hotbar);
            }
        });

        if (reload) Bukkit.getOnlinePlayers().forEach(this::giveHotbar);
    }
}
