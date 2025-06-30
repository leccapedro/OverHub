package studio.overmine.overhub.controllers;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.bukkit.entity.Player;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import studio.overmine.overhub.OverHub;
import studio.overmine.overhub.models.user.IUser;
import studio.overmine.overhub.models.user.User;
import studio.overmine.overhub.models.user.storage.UserMongo;
import lombok.Getter;

@Getter
public class UserController {

    private final OverHub plugin;
    private final IUser user;
    private final Map<UUID, User> users;
    private final Cache<String, User> nameUserCache;
    private final Cache<UUID, User> uuidUserCache;

    public UserController(OverHub plugin, MongoController mongoController) {
        this.plugin = plugin;
        this.users = new ConcurrentHashMap<>();
        this.nameUserCache = CacheBuilder.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .build();
        this.uuidUserCache = CacheBuilder.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .build();
        this.user = new UserMongo(this, mongoController);
    }

    public User getUser(UUID uuid) {
        return users.get(uuid);
    }

    public User getUserUUIDFromDatabase(UUID uuid, boolean load) {
        User existingUser = users.get(uuid);

        if (existingUser != null) {
            if (load) loadUser(existingUser);
            return existingUser;
        }

        User cachedUser = uuidUserCache.getIfPresent(uuid);

        if (cachedUser != null) {
            if (load) loadUser(cachedUser);
            return cachedUser;
        }

        return this.user.getUser(uuid, load);
    }

    public User getUserNameFromDatabase(String name, boolean load) {
        String key = name.toLowerCase();
        Player player = plugin.getServer().getPlayer(key);

        if (player != null) {
            User existingUser = getUser(player.getUniqueId());
            if (user != null && load) loadUser(existingUser);

            return existingUser;
        }

        User cachedUser = nameUserCache.getIfPresent(key);

        if (cachedUser != null) {
            if (load) loadUser(cachedUser);
            return cachedUser;
        }

        return this.user.getUser(key, load);
    }

    public User createUser(UUID uuid, String name) {
        User newUser = this.user.createUser(uuid, name);
        users.put(uuid, newUser);
        return newUser;
    }

    public void saveUser(User user) {
        CompletableFuture.runAsync(() -> this.user.saveUser(user));
    }

    public void loadUser(User user) {
        this.user.loadUser(user);
    }

    public void destroyUser(User user) {
        users.remove(user.getUuid());
        nameUserCache.invalidate(user.getName().toLowerCase());
    }
}
