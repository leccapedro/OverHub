package studio.overmine.overhub.models.user;

import java.util.Arrays;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import studio.overmine.overhub.OverHub;
import studio.overmine.overhub.utilities.FileConfig;

@Getter
public class User {

    private UUID uuid;
    private String name;
    private VisibilityType visibilityType;
    private FileConfig dataFile;
    private int parkourHS;
    private ItemStack[] pvpHotbar;

    public User(OverHub plugin, UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        this.visibilityType = VisibilityType.ALL;
        this.dataFile = new FileConfig(plugin, "data/user-data/" + uuid.toString() + ".yml");
        this.parkourHS = 0;
        this.pvpHotbar = new ItemStack[36];
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

    public void setPvpHotbar(ItemStack[] hotbar) {
        if (hotbar == null) {
            this.pvpHotbar = new ItemStack[36];
            return;
        }

        int size = Math.min(36, hotbar.length);
        ItemStack[] clone = new ItemStack[36];
        for (int i = 0; i < size; i++) {
            clone[i] = hotbar[i] != null ? hotbar[i].clone() : null;
        }
        if (size < 36) {
            Arrays.fill(clone, size, 36, null);
        }
        this.pvpHotbar = clone;
    }

    public boolean hasPvpHotbar() {
        if (pvpHotbar == null) {
            return false;
        }
        for (ItemStack itemStack : pvpHotbar) {
            if (itemStack != null) {
                return true;
            }
        }
        return false;
    }

    public ItemStack[] getPvpHotbarClone() {
        if (pvpHotbar == null) {
            return new ItemStack[36];
        }
        ItemStack[] clone = new ItemStack[pvpHotbar.length];
        for (int i = 0; i < pvpHotbar.length; i++) {
            clone[i] = pvpHotbar[i] != null ? pvpHotbar[i].clone() : null;
        }
        return clone;
    }
}
