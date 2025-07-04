package studio.overmine.overhub.utilities.cooldown;

import com.google.common.collect.Table;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;
import java.util.UUID;

public class CooldownTask extends BukkitRunnable {

    private final JavaPlugin plugin;

    public CooldownTask(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        if (CooldownUtil.getCooldowns().isEmpty()) {
            cancel();
            return;
        }

        Iterator<Table.Cell<UUID, String, Long>> iterator = CooldownUtil.getCooldowns().cellSet().iterator();

        while (iterator.hasNext()) {
            Table.Cell<UUID, String, Long> table = iterator.next();

            if (table.getValue() <= 0) {
                iterator.remove();
                CooldownUtil.removeCooldown(table.getRowKey(), table.getColumnKey());
            }
        }

        if (CooldownUtil.getCooldowns().isEmpty()) {
            cancel();
        }
    }

    public void start() {
        this.runTaskTimer(plugin, 0L, 20L);
    }
}
