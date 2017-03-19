package org.smallbox.faraway.core.game.model;

import org.smallbox.faraway.util.Utils;

/**
 * Created by Alex on 08/10/2015.
 */
// TODO concurrent modification exception
public class ObjectModel {
    public final int _id;

    public ObjectModel() {
        _id = Utils.getUUID();
    }

    public ObjectModel(int id) {
        _id = Utils.getUUID(id);
    }
}
