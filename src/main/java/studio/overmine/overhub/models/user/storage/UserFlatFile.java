package studio.overmine.overhub.models.user.storage;

import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import studio.overmine.overhub.OverHub;
import studio.overmine.overhub.controllers.HotbarController;
import studio.overmine.overhub.models.user.IUser;
import studio.overmine.overhub.models.user.User;
import studio.overmine.overhub.models.user.VisibilityType;
import studio.overmine.overhub.models.user.types.PvpState;
import studio.overmine.overhub.utilities.FileConfig;

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
        user.setParkourHS(section.getInt("parkour-score"));
        user.setPvpEnabled(section.getBoolean("pvp.enabled"));
        user.setPvpState(PvpState.fromString(section.getString("pvp.state")));
        user.setPvpKills(section.getInt("pvp.kills"));
        user.setPvpKillStreak(section.getInt("pvp.kill-streak"));
        user.setLastHitBy(section.getString("pvp.last-hit-by"));

        ConfigurationSection pvpLayoutSection = section.getConfigurationSection("pvp.layout.hotbar");
        if (pvpLayoutSection == null) {
            pvpLayoutSection = section.getConfigurationSection("pvp-layout.hotbar");
        }
        if (pvpLayoutSection != null) {
            ItemStack[] layout = new ItemStack[36];
            boolean hasItems = false;
            for (int i = 0; i < layout.length; i++) {
                ItemStack itemStack = pvpLayoutSection.getItemStack(String.valueOf(i));
                layout[i] = itemStack;
                if (itemStack != null) {
                    hasItems = true;
                }
            }

            if (hasItems) {
                HotbarController hotbarController = plugin.getHotbarController();
                if (hotbarController != null) {
                    hotbarController.migrateLegacyPvpLayout(layout);
                }
            }
        }
    }

    public void toSavable(User user) {
        FileConfig userDataFile = user.getDataFile();
        ConfigurationSection section = userDataFile.getConfiguration();

        section.set("name", user.getName());
        section.set("visibility", user.getVisibilityType().name());
        section.set("parkour-score", user.getParkourHS());
        section.set("pvp.enabled", user.isPvpEnabled());
        section.set("pvp.state", user.getPvpState().name());
        section.set("pvp.kills", user.getPvpKills());
        section.set("pvp.kill-streak", user.getPvpKillStreak());
        section.set("pvp.last-hit-by", user.getLastHitBy());

        section.set("pvp-layout.hotbar", null);
        section.set("pvp.layout.hotbar", null);

        userDataFile.save();
    }
}