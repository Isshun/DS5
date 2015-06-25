package org.smallbox.faraway.data.factory.map;

import org.smallbox.faraway.data.serializer.LoadListener;
import org.smallbox.faraway.game.model.item.ParcelModel;

/**
 * Created by Alex on 20/06/2015.
 */
public interface IMapFactory {
    void create(ParcelModel[][][] parcels, int width, int height, LoadListener loadListener);
}
