package studio.overmine.overhub.models.user.storage;

import org.bukkit.configuration.ConfigurationSection;
import studio.overmine.overhub.OverHub;
import studio.overmine.overhub.models.user.IUser;
import studio.overmine.overhub.models.user.User;
import studio.overmine.overhub.models.user.VisibilityType;
import studio.overmine.overhub.utilities.FileConfig;

import java.util.UUID;

public class UserFlatFile implements IUser {

    private final OverHub plugin;

    public UserFlatFile(OverHub plugin) {
        this.plugin = plugin;
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
        user.setName(section.getString("name"));
        user.setVisibilityType(VisibilityType.valueOf(section.getString("visibility")));
    }

    public void toSavable(User user) {
        FileConfig userDataFile = user.getDataFile();
        ConfigurationSection section = userDataFile.getConfiguration();

        section.set("name", user.getName());
        section.set("visibility", user.getVisibilityType().name());

        userDataFile.save();
    }
}
