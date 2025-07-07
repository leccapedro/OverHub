package studio.overmine.overhub.tasks;

import org.bukkit.Bukkit;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import studio.overmine.overhub.OverHub;

import java.util.List;

/**
 * @author Risas
 * @date 06-07-2025
 * @discord https://risas.me/discord
 */
public class BossBarTask extends BukkitRunnable {

    private final OverHub plugin;
    private final List<BossBar> bossBars;
    private double progress;
    private int index;
    private BossBar bossBar;

    public BossBarTask(OverHub plugin, List<BossBar> bossBars) {
        this.plugin = plugin;
        this.bossBars = bossBars;
        this.progress = 1.0;
        this.index = 0;
        this.bossBar = getNextBossBar();
    }

    @Override
    public void run() {
        if (progress <= 0) {
            progress = 1.0;
            bossBar = getNextBossBar();
        }

        bossBar.setProgress(progress);
        progress -= 0.1;
    }

    public void addBossBar(Player player) {
        bossBar.addPlayer(player);
    }

    public void destroy() {
        if (bossBar != null) {
            bossBar.removeAll();
        }
    }

    public BossBar getNextBossBar() {
        destroy();

        BossBar next = bossBars.get(index);
        next.setVisible(true);

        Bukkit.getOnlinePlayers().forEach(next::addPlayer);

        index = (index + 1) % bossBars.size();
        return next;
    }

    public void start() {
        this.runTaskTimerAsynchronously(plugin, 0L, 20L);
    }
}
