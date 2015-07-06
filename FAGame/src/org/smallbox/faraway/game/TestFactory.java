package org.smallbox.faraway.game;

import org.smallbox.faraway.data.factory.map.MapFactory;
import org.smallbox.faraway.data.serializer.LoadListener;
import org.smallbox.faraway.game.model.GameData;
import org.smallbox.faraway.game.model.item.ItemInfo;
import org.smallbox.faraway.game.model.item.ParcelModel;
import org.smallbox.faraway.game.model.item.ResourceModel;

/**
 * Created by Alex on 06/07/2015.
 */
public class TestFactory extends MapFactory {

    @Override
    public void onCreate(ParcelModel[][][] parcels, int mapWidth, int mapHeight, LoadListener loadListener) {
        Game.getWorldManager().getParcelList().forEach(parcel -> {
            parcel.setType(0);
            parcel.setResource(null);
        });

        ItemInfo itemRock = GameData.getData().getItemInfo("base.rock");

        new MidpointDisplacement(MapGenConfig.CONFIGS[1]).create((parcel) -> {
            ResourceModel resourceModel = new ResourceModel(itemRock);
            resourceModel.setValue(10);
            parcel.setResource(resourceModel);
            parcel.setType(2);
        });

        new MidpointDisplacement(MapGenConfig.CONFIGS[3]).create((parcel) -> {
            if (parcel.getType() == 2) {
                parcel.setResource(null);
                parcel.setType(3);
            }
        });

        new MidpointDisplacement(MapGenConfig.CONFIGS[3]).create((parcel) -> {
            if (parcel.getType() == 2) {
                parcel.setResource(null);
                parcel.setType(4);
            }
        });

        new MidpointDisplacement(MapGenConfig.CONFIGS[2]).create((parcel) -> {
            if (parcel.getResource() != null) {
                parcel.setResource(null);
                parcel.setType(5);
            }
        });

        new MidpointDisplacement(MapGenConfig.CONFIGS[4]).create((parcel) -> {
            if (parcel.getResource() != null) {
                parcel.setResource(null);
                parcel.setType(6);
            }
        });

        new MidpointDisplacement(MapGenConfig.CONFIGS[5]).create((parcel) -> {
            if (parcel.getResource() != null) {
                parcel.setResource(null);
                parcel.setType(7);
            }
        });
    }
}
