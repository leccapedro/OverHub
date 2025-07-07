package studio.overmine.overhub.models.user;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.Setter;
import studio.overmine.overhub.OverHub;
import studio.overmine.overhub.utilities.FileConfig;

@Getter @Setter
public class User {

    private UUID uuid;
    private String name;
    private VisibilityType visibilityType;
    private FileConfig dataFile;
    private int parkourScore;

    public User(OverHub plugin, UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        this.visibilityType = VisibilityType.ALL;
        this.dataFile = new FileConfig(plugin, "data/user-data/" + uuid.toString() + ".yml");
        this.parkourScore = 0;
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
}
