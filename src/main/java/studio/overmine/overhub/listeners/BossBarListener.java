package studio.overmine.overhub.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import studio.overmine.overhub.controllers.BossBarController;

/**
 * @author Risas
 * @date 06-07-2025
 * @discord https://risas.me/discord
 */
public class BossBarListener implements Listener {

    private final BossBarController bossBarController;

    public BossBarListener(BossBarController bossBarController) {
        this.bossBarController = bossBarController;
    }

    @EventHandler
    public void onBossBarJoin(PlayerJoinEvent event) {
        if (!bossBarController.isBossBarTaskRunning()) return;
        bossBarController.getBossBarTask().addBossBar(event.getPlayer());
    }
}
