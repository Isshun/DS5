package org.smallbox.faraway.module.flora;

import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.client.ModuleRenderer;
import org.smallbox.faraway.core.module.GameModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.world.model.ParcelModel;
import org.smallbox.faraway.core.world.model.PlantModel;
import org.smallbox.faraway.module.world.WorldModule;
import org.smallbox.faraway.module.world.WorldModuleObserver;

import java.util.Collection;
import java.util.LinkedList;

import static org.smallbox.faraway.core.game.modelInfo.ItemInfo.ItemInfoPlant.GrowingInfo;

@ModuleRenderer(FloraTopRenderer.class)
public class FloraModule extends GameModule<FloraModuleObserver> {
    @BindModule
    private WorldModule _world;

    private Collection<PlantModel> _plants;

    @Override
    public void onGameCreate(Game game) {
        _plants = new LinkedList<>();

        _world.addObserver(new WorldModuleObserver() {
// TODO
            //            @Override
//            public void onRemoveResource(MapObjectModel mapObject) {
//                if (mapObject instanceof PlantModel) {
//                    removeResource((PlantModel) mapObject);
//                }
//            }
//
//            @Override
//            public void onAddResource(MapObjectModel resource) {
//                if (resource instanceof PlantModel && resource.getInfo().plant != null) {
//                    _plants.addSubJob((PlantModel) resource);
//                }
//            }

            @Override
            public PlantModel putObject(ParcelModel parcel, ItemInfo itemInfo, int data, boolean complete) {
                if (itemInfo.isPlant) {
                    return putPlant(parcel, itemInfo, data);
                }
                return null;
            }
        });
    }
}
