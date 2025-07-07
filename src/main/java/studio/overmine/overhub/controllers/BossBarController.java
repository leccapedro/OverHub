package studio.overmine.overhub.controllers;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.ConfigurationSection;
import studio.overmine.overhub.OverHub;
import studio.overmine.overhub.tasks.BossBarTask;
import studio.overmine.overhub.utilities.ChatUtil;
import studio.overmine.overhub.utilities.FileConfig;

import java.util.*;

/**
 * @author Risas
 * @date 06-07-2025
 * @discord https://risas.me/discord
 */
public class BossBarController {

    private final OverHub plugin;
    private final FileConfig configFile;
    private final List<BossBar> bossBars;
    @Getter private BossBarTask bossBarTask;

    public BossBarController(OverHub plugin, FileConfig configFile) {
        this.plugin = plugin;
        this.configFile = configFile;
        this.bossBars = new ArrayList<>();
        this.onReload();
    }

    public boolean isBossBarTaskRunning() {
        return bossBarTask != null;
    }

    public void onReload() {
        if (bossBarTask != null) {
            bossBarTask.destroy();
            bossBarTask.cancel();
        }

        bossBars.clear();

        ConfigurationSection section = configFile.getConfigurationSection("boss-bar-system.types");
        if (section == null) throw new IllegalStateException("Boss bar types section is missing in the config file.");

        section.getKeys(false).forEach(bossBarId -> {
            ConfigurationSection bossBarSection = section.getConfigurationSection(bossBarId);
            if (bossBarSection == null) throw new IllegalStateException("Boss bar section for " + bossBarId + " is missing in the config file.");

            BossBar bossBar = Bukkit.createBossBar(
                    ChatUtil.translate(bossBarSection.getString("title")),
                    BarColor.valueOf(bossBarSection.getString("color")),
                    BarStyle.valueOf(bossBarSection.getString("style"))
            );

            bossBars.add(bossBar);
        });

        if (!bossBars.isEmpty()) {
            bossBarTask = new BossBarTask(plugin, bossBars);
            bossBarTask.start();
        }
    }
}
