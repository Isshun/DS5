package org.smallbox.faraway.common;

/**
 * Created by Alex on 08/10/2015.
 */
// TODO concurrent modification exception
public class ObjectModel {
    public final int _id;

    public ObjectModel() {
        _id = UUIDUtils.getUUID();
    }

    public ObjectModel(int id) {
        _id = UUIDUtils.getUUID(id);
    }
}
