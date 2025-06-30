package studio.overmine.overhub.models.user;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class User {

    private UUID uuid;
    private String name;
    private VisibilityType visibilityType;

    public User(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        this.visibilityType = VisibilityType.ALL;
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

            if (onlinePlayer.hasPermission("crowned.donator")) {
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

            if (onlinePlayer.hasPermission("crowned.staff")) {
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
}
