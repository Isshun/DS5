package org.smallbox.faraway.data.factory.map;

import org.smallbox.faraway.data.serializer.LoadListener;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.manager.WorldManager;
import org.smallbox.faraway.game.model.item.ParcelModel;

/**
 * Created by Alex on 20/06/2015.
 */
public abstract class MapFactory {
    public abstract void onCreate(ParcelModel[][][] parcels, int width, int height, LoadListener loadListener);

    public void create(WorldManager worldManager, int width, int height, LoadListener loadListener) {
        if (worldManager.getParcels() == null) {
            // Initialize world map
            worldManager.init(width, height);
//            throw new RuntimeException("WorldManager as already been initialized");
        }

        ParcelModel[][][] parcels = worldManager.getParcels();

        // Call factory method
        onCreate(parcels, width, height, loadListener);

        // Notify managers of map objects
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                final ParcelModel parcel = parcels[x][y][0];
                if (parcel.getStructure() != null) {
                    Game.getInstance().notify(observer -> observer.onAddStructure(parcel.getStructure()));
                }
                if (parcel.getResource() != null) {
                    Game.getInstance().notify(observer -> observer.onAddResource(parcel.getResource()));
                }
                if (parcel.getItem() != null) {
                    Game.getInstance().notify(observer -> observer.onAddItem(parcel.getItem()));
                }
                if (parcel.getConsumable() != null) {
                    Game.getInstance().notify(observer -> observer.onAddConsumable(parcel.getConsumable()));
                }
            }
        }
    }
}
