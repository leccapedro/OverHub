package studio.overmine.overhub.listeners;

import com.cryptomorin.xseries.XSound;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Risas
 */
public class DoubleJumpListener implements Listener {

    private final Set<Player> jumpers;

    public DoubleJumpListener() {
        this.jumpers = new HashSet<>();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (player.getGameMode() != GameMode.CREATIVE && player.getGameMode() != GameMode.SPECTATOR) {
            player.setAllowFlight(true);
        }
    }

    @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();

        if (player.getGameMode() == GameMode.CREATIVE
                || player.getGameMode() == GameMode.SPECTATOR
                || jumpers.contains(player)) {
            return;
        }

        event.setCancelled(true);
        player.setAllowFlight(false);

        Vector direction = player.getLocation().getDirection();
        double boost = 2.0;

        Vector velocity = direction.multiply(boost).setY(boost);
        player.setVelocity(velocity);
        XSound.BLOCK_PISTON_EXTEND.play(player);

        jumpers.add(player);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (player.getAllowFlight()
                || player.getGameMode() == GameMode.CREATIVE
                || player.getGameMode() == GameMode.SPECTATOR) {
            return;
        }

        if (jumpers.contains(player) && player.isSneaking()) {
            Vector boostVelocity = player.getLocation().getDirection().multiply(2);

            player.setVelocity(player.getVelocity().add(boostVelocity));
            XSound.ENTITY_BAT_TAKEOFF.play(player);
            jumpers.remove(player);
        }

        if (player.isOnGround() || blockBelowPlayer(player)) {
            player.setAllowFlight(true);
            jumpers.remove(player);
        }
    }

    private boolean blockBelowPlayer(Player player) {
        return player.getLocation().subtract(0, 0.1, 0).getBlock().getType() != Material.AIR;
    }
}
