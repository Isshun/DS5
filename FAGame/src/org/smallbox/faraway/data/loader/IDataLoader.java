package org.smallbox.faraway.data.loader;

import org.smallbox.faraway.game.model.GameData;

/**
 * Created by Alex on 18/06/2015.
 */
public interface IDataLoader {
    void reloadIfNeeded(GameData data);
    void load(GameData data);
}
