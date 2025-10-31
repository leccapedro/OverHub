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
import studio.overmine.overhub.utilities.BukkitUtil;
import studio.overmine.overhub.utilities.FileConfig;
import studio.overmine.overhub.utilities.item.ItemBuilder;

public class HotbarController {

    private static final int DEFAULT_STORAGE_SIZE = 36;

    private final OverHub plugin;
    private final FileConfig hotbarFile;
    private final FileConfig pvpInventoryFile;
    private final UserController userController;
    private final Map<String, Hotbar> hotbarMap;
    private final ConcurrentMap<UUID, ItemStack[]> lobbyInventorySnapshots;
    private final ConcurrentMap<UUID, ItemStack> lobbyOffhandSnapshots;
    private volatile ItemStack[] globalPvpLayout;

    public HotbarController(OverHub plugin) {
        this.plugin = plugin;
        this.hotbarFile = plugin.getFileConfig("hotbar");
        this.pvpInventoryFile = plugin.getFileConfig("pvp-inventory");
        this.userController = plugin.getUserController();
        this.hotbarMap = new LinkedHashMap<>();
        this.lobbyInventorySnapshots = new ConcurrentHashMap<>();
        this.lobbyOffhandSnapshots = new ConcurrentHashMap<>();
        this.globalPvpLayout = new ItemStack[0];
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
        lobbyInventorySnapshots.put(uuid, createStorageSnapshot(inventory));
        if (BukkitUtil.SERVER_VERSION >= 9) {
            lobbyOffhandSnapshots.put(uuid, cloneItem(inventory.getItemInOffHand()));
            inventory.setItemInOffHand(null);
        } else {
            lobbyOffhandSnapshots.remove(uuid);
        }

        clearStorageContents(inventory);

        boolean hasLayout = false;
        ItemStack[] globalLayout = getGlobalPvpLayoutClone();
        if (hasItems(globalLayout)) {
            setStorageContents(inventory, globalLayout);
            hasLayout = true;
        }

        User user = userController.getUser(uuid);
        if (user != null && user.hasSavedPvpLayout()) {
            setStorageContents(inventory, user.getSavedPvpLayoutClone());
            hasLayout = true;
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
        ItemStack[] storedContents = lobbyInventorySnapshots.remove(uuid);
        if (storedContents != null) {
            setStorageContents(inventory, cloneStorageContents(storedContents));
        } else {
            clearStorageContents(inventory);
        }

        if (BukkitUtil.SERVER_VERSION >= 9) {
            ItemStack offhandItem = lobbyOffhandSnapshots.remove(uuid);
            inventory.setItemInOffHand(cloneItem(offhandItem));
        } else {
            lobbyOffhandSnapshots.remove(uuid);
        }

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

    private ItemStack cloneItem(ItemStack itemStack) {
        return itemStack == null ? null : itemStack.clone();
    }

    public ItemStack[] createStorageSnapshot(PlayerInventory inventory) {
        return cloneStorageContents(readStorageContents(inventory));
    }

    private ItemStack[] readStorageContents(PlayerInventory inventory) {
        if (BukkitUtil.SERVER_VERSION >= 9) {
            return inventory.getStorageContents();
        }

        int storageSize = inventory.getSize();
        ItemStack[] source = inventory.getContents();
        ItemStack[] storageContents = new ItemStack[storageSize];
        if (source == null) {
            return storageContents;
        }

        int length = Math.min(storageSize, source.length);
        for (int i = 0; i < length; i++) {
            storageContents[i] = source[i];
        }
        return storageContents;
    }

    private void setStorageContents(PlayerInventory inventory, ItemStack[] contents) {
        int storageSize = getStorageSize(inventory);
        ItemStack[] normalizedContents = normalizeStorageContents(contents, storageSize);

        if (BukkitUtil.SERVER_VERSION >= 9) {
            inventory.setStorageContents(normalizedContents);
            return;
        }

        for (int i = 0; i < storageSize; i++) {
            inventory.setItem(i, normalizedContents[i]);
        }
    }

    private void clearStorageContents(PlayerInventory inventory) {
        setStorageContents(inventory, new ItemStack[getStorageSize(inventory)]);
    }

    private ItemStack[] cloneStorageContents(ItemStack[] contents) {
        if (contents == null) {
            return new ItemStack[0];
        }

        ItemStack[] clone = new ItemStack[contents.length];
        for (int i = 0; i < contents.length; i++) {
            clone[i] = cloneItem(contents[i]);
        }
        return clone;
    }

    private ItemStack[] normalizeStorageContents(ItemStack[] contents, int size) {
        ItemStack[] normalized = new ItemStack[size];
        if (contents == null) {
            return normalized;
        }

        int length = Math.min(size, contents.length);
        for (int i = 0; i < length; i++) {
            normalized[i] = cloneItem(contents[i]);
        }
        return normalized;
    }

    private int getStorageSize(PlayerInventory inventory) {
        if (BukkitUtil.SERVER_VERSION >= 9) {
            return inventory.getStorageContents().length;
        }
        return inventory.getSize();
    }

    public ItemStack[] getGlobalPvpLayoutClone() {
        return cloneStorageContents(globalPvpLayout);
    }

    public boolean hasGlobalPvpLayout() {
        return hasItems(globalPvpLayout);
    }

    public synchronized void saveGlobalPvpLayout(ItemStack[] contents) {
        if (pvpInventoryFile == null) {
            return;
        }

        int size = contents != null ? contents.length : DEFAULT_STORAGE_SIZE;
        ItemStack[] normalized = normalizeStorageContents(contents, size);

        pvpInventoryFile.set("layout.size", size);

        pvpInventoryFile.set("layout.hotbar", null);
        ConfigurationSection layoutSection = pvpInventoryFile.getConfiguration().createSection("layout.hotbar");

        for (int i = 0; i < normalized.length; i++) {
            layoutSection.set(String.valueOf(i), normalized[i]);
        }

        pvpInventoryFile.save();
        globalPvpLayout = cloneStorageContents(normalized);
    }

    public void migrateLegacyPvpLayout(ItemStack[] layout) {
        if (!hasGlobalPvpLayout() && hasItems(layout)) {
            saveGlobalPvpLayout(layout);
            plugin.getLogger().info("Migrated legacy PvP layout to data/pvp-inventory.yml");
        }
    }

    public final void onReload(boolean reload) {
        if (pvpInventoryFile != null) {
            pvpInventoryFile.reload();
            loadGlobalPvpLayout();
        }

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

    private void loadGlobalPvpLayout() {
        ConfigurationSection layoutSection = pvpInventoryFile.getConfiguration().getConfigurationSection("layout.hotbar");
        int fallbackSize = Math.max(DEFAULT_STORAGE_SIZE, pvpInventoryFile.getConfiguration().getInt("layout.size", DEFAULT_STORAGE_SIZE));
        globalPvpLayout = readLayout(layoutSection, fallbackSize);
    }

    private ItemStack[] readLayout(ConfigurationSection section, int fallbackSize) {
        if (section == null || section.getKeys(false).isEmpty()) {
            return new ItemStack[0];
        }

        int maxIndex = section.getKeys(false).stream()
                .map(key -> {
                    try {
                        return Integer.parseInt(key);
                    } catch (NumberFormatException ex) {
                        return -1;
                    }
                })
                .filter(index -> index >= 0)
                .mapToInt(Integer::intValue)
                .max()
                .orElse(-1);

        if (maxIndex < 0) {
            return new ItemStack[0];
        }

        int size = Math.max(fallbackSize, maxIndex + 1);
        ItemStack[] layout = new ItemStack[size];
        for (int i = 0; i < size; i++) {
            layout[i] = section.getItemStack(String.valueOf(i));
        }
        return layout;
    }

    private boolean hasItems(ItemStack[] contents) {
        if (contents == null) {
            return false;
        }
        for (ItemStack itemStack : contents) {
            if (itemStack != null) {
                return true;
            }
        }
        return false;
    }
}