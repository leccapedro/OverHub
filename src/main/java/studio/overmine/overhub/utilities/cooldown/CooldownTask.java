package studio.overmine.overhub.utilities.cooldown;

import com.google.common.collect.Table;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Iterator;
import java.util.UUID;

public class CooldownTask implements Runnable {

    private final JavaPlugin plugin;
    private int id = -1;

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
            UUID uuid = table.getRowKey();
            String name = table.getColumnKey();
            long remainingTime = table.getValue();

            if (remainingTime <= 0) {
                iterator.remove();
                CooldownUtil.removeCooldown(uuid, name);
            }
        }

        if (CooldownUtil.getCooldowns().isEmpty()) {
            cancel();
        }
    }

    public void start() {
        if (!isRunning()) {
            this.id = Bukkit.getScheduler().runTaskTimer(plugin, this, 0L, 20L).getTaskId();
        }
    }

    public void cancel() {
        if (isRunning()) {
            Bukkit.getScheduler().cancelTask(id);
            id = -1;
        }
    }

    public boolean isRunning() {
        return id != -1 && Bukkit.getScheduler().isCurrentlyRunning(id);
    }
}
