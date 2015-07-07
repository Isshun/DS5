package org.smallbox.faraway.game;

import org.smallbox.faraway.data.factory.map.MapFactory;
import org.smallbox.faraway.data.serializer.LoadListener;
import org.smallbox.faraway.game.model.GameData;
import org.smallbox.faraway.game.model.item.ParcelModel;
import org.smallbox.faraway.game.model.item.ResourceModel;
import org.smallbox.faraway.game.model.planet.RegionInfo;
import org.smallbox.faraway.util.Log;

import java.util.Random;

/**
 * Created by Alex on 06/07/2015.
 */
public class TestFactory extends MapFactory {

    @Override
    public void onCreate(ParcelModel[][][] parcels, int mapWidth, int mapHeight, LoadListener loadListener) {
        // Initialize game map
        Game.getWorldManager().getParcelList().forEach(parcel -> {
            parcel.setType(0);
            parcel.setResource(null);
        });

        // Add region terrains
        for (RegionInfo.RegionTerrain terrain: Game.getInstance().getRegion().getInfo().terrains) {
            if (terrain.pattern != null) {
                Log.notice("Create map with pattern: " + terrain.pattern);
                new MidpointDisplacement(MapGenConfig.get(terrain.pattern)).create(parcel ->
                        applyToParcel(terrain, parcel));
            } else {
                Game.getWorldManager().getParcelList().forEach(parcel ->
                        applyToParcel(terrain, parcel));
            }
        }

        // Notify observers
        Game.getWorldManager().getParcelList().forEach(parcel -> {
            if (parcel.getResource() != null) {
                Game.getInstance().notify(observer -> observer.onAddResource(parcel.getResource()));
                Game.getWorldManager().getResources().add(parcel.getResource());
            }
        });
    }

    private void applyToParcel(RegionInfo.RegionTerrain terrain, ParcelModel parcel) {
        if (terrain.condition == null
                || ("rock".equals(terrain.condition) && parcel.getResource() != null && parcel.getResource().isRock())
                || ("ground".equals(terrain.condition) && (parcel.getResource() == null || !parcel.getResource().isRock()))) {

            // Set type
            if (terrain.typeId != -1) {
                parcel.setType(terrain.typeId);
            }

            // Add resource
            if (terrain.resource != null) {
                ResourceModel resourceModel = new ResourceModel(GameData.getData().getItemInfo(terrain.resource));
                if (terrain.quantity != null && terrain.quantity[1] != terrain.quantity[0]) {
                    resourceModel.setValue(new Random().nextInt(terrain.quantity[1] - terrain.quantity[0]) + terrain.quantity[0]);
                } else if (terrain.quantity != null) {
                    resourceModel.setValue(terrain.quantity[0]);
                } else {
                    resourceModel.setValue(10);
                }
                parcel.setResource(resourceModel);
            }
        }
    }

}
