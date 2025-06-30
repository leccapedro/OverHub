package studio.overmine.overhub.models.user.storage;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import studio.overmine.overhub.controllers.MongoController;
import studio.overmine.overhub.controllers.UserController;
import studio.overmine.overhub.database.MongoSavable;
import studio.overmine.overhub.models.user.IUser;
import studio.overmine.overhub.models.user.User;
import studio.overmine.overhub.models.user.VisibilityType;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.UUID;

public class UserMongo implements IUser, MongoSavable<Document> {

    private final UserController userController;
    private final MongoController mongoController;

    public UserMongo(UserController userController, MongoController mongoController) {
        this.userController = userController;
        this.mongoController = mongoController;
    }

    @Override
    public User getUser(String name, boolean load) {
        Document document = find(Filters.eq("name", name));
        if (document == null) return null;

        UUID uuid = UUID.fromString(document.getString("_id"));
        User user = new User(uuid, name);
        if (load) this.loadUser(user, document);

        userController.getNameUserCache().put(name, user);
        return user;
    }

    @Override
    public User getUser(UUID uuid, boolean load) {
        Document document = find(Filters.eq("_id", uuid.toString()));
        if (document == null) return null;

        User user = new User(uuid, document.getString("name"));
        if (load) this.loadUser(user, document);

        userController.getUuidUserCache().put(uuid, user);
        return user;
    }

    @Override
    public User createUser(UUID uuid, String name) {
        return new User(uuid, name);
    }

    @Override
    public void saveUser(User user) {
        mongoController.getUsers().replaceOne(Filters.eq("_id", user.getUuid().toString()), toSavable(user), new ReplaceOptions().upsert(true));
    }

    @Override
    public void loadUser(User user) {
        Document document = find(Filters.eq("_id", user.getUuid().toString()));

        if (document == null) {
            this.saveUser(user);
            return;
        }

        this.loadUser(user, document);
    }

    public void loadUser(User user, Document document) {
        user.setName(document.getString("name"));
        user.setVisibilityType(VisibilityType.valueOf(document.getString("visibility")));
    }

    public Document find(Bson bson) {
        return mongoController.getUsers().find(bson).first();
    }

    @Override
    public Document toSavable(Object object) {
        User user = (User) object;
        Document document = new Document();
        document.put("_id", user.getUuid().toString());
        document.put("name", user.getName());
        document.put("visibility", user.getVisibilityType().name());
        return document;
    }
}
