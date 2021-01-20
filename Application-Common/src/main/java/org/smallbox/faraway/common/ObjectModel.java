package org.smallbox.faraway.common;

import java.util.Random;

// TODO concurrent modification exception
public class ObjectModel {
    public final int _id;
    public float stateTime = new Random().nextFloat();

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
