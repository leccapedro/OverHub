package studio.overmine.overhub.utilities;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class FileConfig {

    private final JavaPlugin plugin;
    private final File file;
    private FileConfiguration configuration;

    public FileConfig(JavaPlugin plugin, String fileName) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), fileName);

        if (!this.file.exists()) {
            this.file.getParentFile().mkdirs();
            if (plugin.getResource(fileName) == null) {
                try {
                    this.file.createNewFile();
                } catch (IOException e) {
                    plugin.getLogger().severe("Failed to create new file " + fileName);
                }
            } else {
                plugin.saveResource(fileName, false);
            }
        }

        this.configuration = YamlConfiguration.loadConfiguration(this.file);
    }

    private <T> T getOrDefault(String path, T defaultValue) {
        if (!configuration.contains(path)) {
            configuration.set(path, defaultValue);
            save();
        }
        return (T) configuration.get(path);
    }

    public double getDouble(String path, double defaultValue) {
        return getOrDefault(path, defaultValue);
    }

    public int getInt(String path, int defaultValue) {
        return getOrDefault(path, defaultValue);
    }

    public boolean getBoolean(String path, boolean defaultValue) {
        return getOrDefault(path, defaultValue);
    }

    public long getLong(String path, long defaultValue) {
        return getOrDefault(path, defaultValue);
    }

    public String getString(String path, String defaultValue) {
        return ChatColor.translateAlternateColorCodes('&', getOrDefault(path, defaultValue));
    }

    public List<String> getStringList(String path, List<String> defaultValue) {
        List<String> list = getOrDefault(path, defaultValue);
        return list.stream().map(s -> ChatColor.translateAlternateColorCodes('&', s)).collect(Collectors.toList());
    }

    public void set(String path, Object value) {
        configuration.set(path, value);
    }

    public void save() {
        try {
            this.configuration.save(this.file);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save file " + file.getName());
        }
    }

    public void reload() {
        this.configuration = YamlConfiguration.loadConfiguration(file);
    }
}
