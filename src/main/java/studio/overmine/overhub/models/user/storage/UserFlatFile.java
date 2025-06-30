package studio.overmine.overhub.models.user.storage;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import studio.overmine.overhub.OverHub;
import studio.overmine.overhub.controllers.UserController;
import studio.overmine.overhub.models.user.IUser;
import studio.overmine.overhub.utilities.FileConfig;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class UserFlatFile implements IUser {

    private final OverHub plugin;
    private final FileConfig userDataFile;
    private final UserController userController;

    public UserFlatFile(OverHub plugin, UserController userController) {
        this.plugin = plugin;
        this.userDataFile = plugin.getFile("user-data");
        this.userController = userController;
    }

    @Override
    public User getUser(String name, boolean load) {
        UUID uuid = getUUIDFromFileByName(name);
        if (uuid == null) return null;

        User user = new User(uuid);

        if (load) {
            FileConfig userDataFile = new FileConfig(plugin, "data/user-data/" + uuid + ".yml");
            ConfigurationSection section = userDataFile.getConfiguration();
            this.loadUser(user, section);
        }

        userController.getNameUserCache().put(name, user);
        return user;
    }

    @Override
    public User getUser(UUID uuid, boolean load) {
        User user = new User(uuid);

        if (load) {
            FileConfig userDataFile = new FileConfig(plugin, "data/user-data/" + uuid + ".yml");
            ConfigurationSection section = userDataFile.getConfiguration();
            this.loadUser(user, section);
        }

        userController.getUuidUserCache().put(uuid, user);
        return user;
    }

    @Override
    public User createUser(UUID uuid, String name) {
        return new User(plugin, uuid, name);
    }

    @Override
    public void saveUser(User user) {
        toSavable(user);
    }

    @Override
    public void loadUser(User user) {
        ConfigurationSection section = user.getDataFile().getConfiguration();

        if (!section.getKeys(false).isEmpty()) {
            this.loadUser(user, section);
        }
    }

    public void loadUser(User user, ConfigurationSection section) {
        user.setKills(section.getInt("kills"));
        user.setDeaths(section.getInt("deaths"));
        user.setBalance(section.getInt("balance"));
        user.setReceivedKit(section.getBoolean("receivedKit"));

        ConfigurationSection kitCooldowns = section.getConfigurationSection("kitCooldowns");

        if (kitCooldowns != null) {
            for (String kitName : kitCooldowns.getKeys(false)) {
                KitCooldown kitCooldown = new KitCooldown(kitName);
                kitCooldown.setKitNextClaimDate(section.getString("kitCooldowns." + kitName));

                user.getKitCooldowns().add(kitCooldown);
            }
        }
    }

    public void toSavable(User user) {
        FileConfig userDataFile = user.getDataFile();
        ConfigurationSection section = userDataFile.getConfiguration();

        section.set("name", user.getName());
        section.set("lowerName", user.getLowerName());
        section.set("kills", user.getKills());
        section.set("deaths", user.getDeaths());
        section.set("balance", user.getBalance());
        section.set("kitCooldowns", new HashMap<>());
        section.set("receivedKit", user.isReceivedKit());

        for (KitCooldown kitCooldown : user.getKitCooldowns()) {
            if (kitCooldown.isExpired()) continue;
            section.set("kitCooldowns." + kitCooldown.getName(), kitCooldown.getKitNextClaimDateFormatted());
        }

        userDataFile.save();
    }

    private UUID getUUIDFromFileByName(String name) {
        Path indexFilePath = Paths.get(plugin.getDataFolder().getAbsolutePath(), "data/user-data.yml");
        if (!Files.exists(indexFilePath)) return null;

        try (InputStream inputStream = Files.newInputStream(indexFilePath)) {
            YamlConfiguration indexConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(inputStream));
            String uuidString = indexConfig.getString(name);

            if (uuidString != null) {
                return UUID.fromString(uuidString);
            }
        }
        catch (IOException e) {
            plugin.getLogger().severe("Error while loading user data index file: " + indexFilePath);
        }

        return null;
    }

    public void updateUserNameInIndex(UUID uuid, String newName) {
        ConfigurationSection section = userDataFile.getConfiguration();
        String oldName = null;
        String uuidString = uuid.toString();

        for (String userName : section.getKeys(false)) {
            if (uuidString.equals(section.getString(userName))) {
                oldName = userName;
                break;
            }
        }

        if (oldName != null) {
            section.set(oldName, null);
        }

        section.set(newName.toLowerCase(), uuidString);
        userDataFile.save();
    }

    @Override
    public List<User> getUsersFromDB() {
        ConfigurationSection usersSection = userDataFile.getConfiguration();
        if (usersSection == null) throw new IllegalStateException("User data configuration section is null");

        List<User> users = new ArrayList<>();

        usersSection.getKeys(false).forEach(name -> {
            String uuidStr = usersSection.getString(name);
            if (uuidStr == null || uuidStr.isEmpty()) return;

            User user = new User(UUID.fromString(uuidStr));
            FileConfig userDataFile = new FileConfig(plugin, "data/user-data/" + user.getUuid() + ".yml");

            user.setName(userDataFile.getString("name"));
            user.setKills(userDataFile.getInt("kills"));
            user.setDeaths(userDataFile.getInt("deaths"));

            users.add(user);
        });

        return users;
    }
}
