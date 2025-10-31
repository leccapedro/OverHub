package studio.overmine.overhub.models.user;

import java.util.Arrays;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import studio.overmine.overhub.OverHub;
import studio.overmine.overhub.models.user.types.PvpState;
import studio.overmine.overhub.utilities.FileConfig;

@Getter
public class User {

    private UUID uuid;
    private String name;
    private VisibilityType visibilityType;
    private FileConfig dataFile;
    private int parkourHS;
    private boolean pvpEnabled;
    private PvpState pvpState;
    private int pvpKills;
    private int pvpKillStreak;
    private String lastHitBy;
    private ItemStack[] savedPvpLayout;

    public User(OverHub plugin, UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        this.visibilityType = VisibilityType.ALL;
        this.dataFile = new FileConfig(plugin, "data/user-data/" + uuid.toString() + ".yml");
        this.parkourHS = 0;
        this.pvpEnabled = false;
        this.pvpState = PvpState.INACTIVE;
        this.pvpKills = 0;
        this.pvpKillStreak = 0;
        this.lastHitBy = null;
        this.savedPvpLayout = new ItemStack[36];
    }

    public void executeCurrentVisibility() {
        switch (visibilityType) {
            case ALL:
                executeAllVisibility();
                break;
            case DONATOR:
                executeDonatorVisibility();
                break;
            case STAFF:
                executeStaffVisibility();
                break;
            case NONE:
                executeNoneVisibility();
                break;
        }
    }

    private void executeAllVisibility() {
        Player player = Bukkit.getPlayer(uuid);
        Bukkit.getOnlinePlayers().forEach(player::showPlayer);
    }

    private void executeDonatorVisibility() {
        Player player = Bukkit.getPlayer(uuid);
        Bukkit.getOnlinePlayers().forEach(onlinePlayer -> {
            if (player.equals(onlinePlayer)) return;

            if (onlinePlayer.hasPermission("overhub.donator")) {
                player.showPlayer(onlinePlayer);
            }
            else {
                player.hidePlayer(onlinePlayer);
            }
        });
    }

    private void executeStaffVisibility() {
        Player player = Bukkit.getPlayer(uuid);
        Bukkit.getOnlinePlayers().forEach(onlinePlayer -> {
            if (player.equals(onlinePlayer)) return;

            if (onlinePlayer.hasPermission("overhub.staff")) {
                player.showPlayer(onlinePlayer);
            }
            else {
                player.hidePlayer(onlinePlayer);
            }
        });
    }

    private void executeNoneVisibility() {
        Player player = Bukkit.getPlayer(uuid);
        Bukkit.getOnlinePlayers().forEach(player::hidePlayer);
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setVisibilityType(VisibilityType visibilityType) {
        this.visibilityType = visibilityType;
    }

    public void setParkourHS(int parkourHS) {
        this.parkourHS = parkourHS;
    }

    public void setPvpEnabled(boolean pvpEnabled) {
        this.pvpEnabled = pvpEnabled;
    }

    public void setPvpState(PvpState pvpState) {
        this.pvpState = pvpState != null ? pvpState : PvpState.INACTIVE;
    }

    public void setPvpKills(int pvpKills) {
        this.pvpKills = Math.max(0, pvpKills);
    }

    public void setPvpKillStreak(int pvpKillStreak) {
        this.pvpKillStreak = Math.max(0, pvpKillStreak);
    }

    public void setLastHitBy(String lastHitBy) {
        this.lastHitBy = lastHitBy;
    }

    public void incrementPvpKills() {
        this.pvpKills++;
    }

    public void incrementPvpKillStreak() {
        this.pvpKillStreak++;
    }

    public void resetPvpKillStreak() {
        this.pvpKillStreak = 0;
    }

    public void clearLastHitBy() {
        this.lastHitBy = null;
    }

    public void setSavedPvpLayout(ItemStack[] layout) {
        if (layout == null) {
            this.savedPvpLayout = new ItemStack[36];
            return;
        }

        int size = Math.min(36, layout.length);
        ItemStack[] clone = new ItemStack[36];
        for (int i = 0; i < size; i++) {
            clone[i] = layout[i] != null ? layout[i].clone() : null;
        }
        if (size < 36) {
            Arrays.fill(clone, size, 36, null);
        }
        this.savedPvpLayout = clone;
    }

    public boolean hasSavedPvpLayout() {
        if (savedPvpLayout == null) {
            return false;
        }
        for (ItemStack itemStack : savedPvpLayout) {
            if (itemStack != null) {
                return true;
            }
        }
        return false;
    }

    public ItemStack[] getSavedPvpLayoutClone() {
        if (savedPvpLayout == null) {
            return new ItemStack[36];
        }
        ItemStack[] clone = new ItemStack[savedPvpLayout.length];
        for (int i = 0; i < savedPvpLayout.length; i++) {
            clone[i] = savedPvpLayout[i] != null ? savedPvpLayout[i].clone() : null;
        }
        return clone;
    }

    @Deprecated
    public void setPvpHotbar(ItemStack[] hotbar) {
        setSavedPvpLayout(hotbar);
    }

    @Deprecated
    public boolean hasPvpHotbar() {
        return hasSavedPvpLayout();
    }

    @Deprecated
    public ItemStack[] getPvpHotbarClone() {
        return getSavedPvpLayoutClone();
    }
}