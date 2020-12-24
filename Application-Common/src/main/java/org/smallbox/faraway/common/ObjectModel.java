package org.smallbox.faraway.common;

// TODO concurrent modification exception
public class ObjectModel {
    public final int _id;

    public ObjectModel() {
        _id = UUIDUtils.getUUID();
    }

    public ObjectModel(int id) {
        _id = UUIDUtils.getUUID(id);
    }

    public int getId() {
        return _id;
    }

}
