package studio.overmine.overhub.models.user;

import java.util.UUID;

public interface IUser {

    User getUser(String name, boolean load);
    User getUser(UUID uuid, boolean load);
    User createUser(UUID uuid, String name);
    void saveUser(User user);
    void loadUser(User user);
}
