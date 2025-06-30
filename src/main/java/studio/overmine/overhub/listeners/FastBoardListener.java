package studio.overmine.overhub.listeners;

import studio.overmine.overhub.OverHub;
import studio.overmine.overhub.controllers.FastBoardController;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class FastBoardListener implements Listener {

    private final FastBoardController fastBoardController;

    public FastBoardListener(OverHub plugin) {
        this.fastBoardController = plugin.getFastBoardController();
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        fastBoardController.createBoardPlayer(event.getPlayer());
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent event) {
        fastBoardController.removeBoardPlayer(event.getPlayer());
    }
}
