package studio.overmine.overhub.controllers;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import studio.overmine.overhub.OverHub;
import studio.overmine.overhub.models.user.IUser;
import studio.overmine.overhub.models.user.User;
import studio.overmine.overhub.models.user.storage.UserFlatFile;
import lombok.Getter;

@Getter
public class UserController {

    private final OverHub plugin;
    private final IUser user;
    private final Map<UUID, User> users;

    public UserController(OverHub plugin) {
        this.plugin = plugin;
        this.users = new ConcurrentHashMap<>();
        this.user = new UserFlatFile(plugin);
    }

    public User getUser(UUID uuid) {
        return users.get(uuid);
    }

    public User createUser(UUID uuid, String name) {
        User user = this.user.createUser(uuid, name);
        users.put(uuid, user);
        return user;
    }

    public void saveUser(User user) {
        CompletableFuture.runAsync(() -> this.user.saveUser(user));
    }

    public void loadUser(User user) {
        this.user.loadUser(user);
    }

    public void destroyUser(User user) {
        users.remove(user.getUuid());
    }
}
