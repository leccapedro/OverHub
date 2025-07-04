package studio.overmine.overhub.models.user;

import java.util.UUID;

public interface IUser {

    User createUser(UUID uuid, String name);
    void saveUser(User user);
    void loadUser(User user);
}
