package studio.overmine.overhub.database;

public interface MongoSavable<T> {
    T toSavable(Object object);
}
