package org.smallbox.faraway.core.data.loader;

import org.smallbox.faraway.core.game.model.Data;

/**
 * Created by Alex on 18/06/2015.
 */
public interface IDataLoader {
    void reloadIfNeeded(Data data);
    void load(Data data);
}
