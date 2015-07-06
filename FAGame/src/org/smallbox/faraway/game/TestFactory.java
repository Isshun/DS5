package org.smallbox.faraway.game;

import org.smallbox.faraway.data.factory.map.MapFactory;
import org.smallbox.faraway.data.serializer.LoadListener;
import org.smallbox.faraway.game.model.GameData;
import org.smallbox.faraway.game.model.item.ItemInfo;
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
        Game.getWorldManager().getParcelList().forEach(parcel -> {
            parcel.setType(1);
            parcel.setResource(null);
        });

        Random r = new Random();
        for (RegionInfo.RegionResource regionResource: Game.getInstance().getRegion().getInfo().resources) {
            ItemInfo itemInfo = GameData.getData().getItemInfo(regionResource.name);
            Log.notice("Create map with pattern: " + regionResource.pattern);
            new MidpointDisplacement(MapGenConfig.get(regionResource.pattern)).create((parcel) -> {
                if (regionResource.terrain == null
                        || ("rock".equals(regionResource.terrain) && parcel.getType() != 0 && parcel.getResource() != null && parcel.getResource().isRock())
                        || ("ground".equals(regionResource.terrain) && parcel.getType() != 0 && (parcel.getResource() == null || !parcel.getResource().isRock()))) {
                    ResourceModel resourceModel = new ResourceModel(itemInfo);
                    if (regionResource.quantity != null && regionResource.quantity[1] != regionResource.quantity[0]) {
                        resourceModel.setValue(r.nextInt(regionResource.quantity[1] - regionResource.quantity[0]) + regionResource.quantity[0]);
                    } else if (regionResource.quantity != null) {
                        resourceModel.setValue(regionResource.quantity[0]);
                    } else {
                        resourceModel.setValue(10);
                    }
                    parcel.setResource(resourceModel);
                }
            });
        }

        Game.getWorldManager().getParcelList().forEach(parcel -> {
            if (parcel.getResource() != null) {
                Game.getInstance().notify(observer -> observer.onAddResource(parcel.getResource()));
                Game.getWorldManager().getResources().add(parcel.getResource());
            }
        });
    }
}
