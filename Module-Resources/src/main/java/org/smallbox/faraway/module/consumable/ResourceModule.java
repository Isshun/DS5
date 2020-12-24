package org.smallbox.faraway.module.consumable;

import org.smallbox.faraway.core.BindModule;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.world.WorldModule;
import org.smallbox.faraway.module.job.JobModule;
import org.smallbox.faraway.module.structure.StructureModule;

public class ResourceModule extends GameModule<ConsumableModuleObserver> {
    @BindModule("base.module.world")
    private WorldModule _world;

    @BindModule("base.module.jobs")
    private JobModule _jobs;

    @BindModule("base.module.structure")
    private StructureModule _structureModel;

//    private Collection<ConsumableModel> _consumables;
//
//    public Collection<ConsumableModel> getConsumables() {
//        return _consumables;
//    }

    @Override
    protected void onGameStart(Game game) {
//        _consumables = new LinkedBlockingQueue<>();

//        _world.addObserver(new WorldModuleObserver() {
//            @Override
//            public MapObjectModel putObject(ParcelModel parcel, ItemInfo itemInfo, int data, boolean complete) {
//                return null;
//            }
//
//            @Override
//            public void onAddParcel(ParcelModel parcel) {
//                if (parcel.hasConsumable()) {
//                    _consumables.add(parcel.getConsumable());
//                }
//            }
//
//            @Override
//            public void onRemoveItem(ParcelModel parcel, ItemModel item) {
//            }
//
//            @Override
//            public void onAddItem(ParcelModel parcel, ItemModel item) {
//            }
//        });
    }

    @Override
    protected void onGameUpdate(Game game, int tick) {
    }

//    @Override
//    public void putObject(ParcelModel parcel, ItemInfo itemInfo, int data, boolean complete) {
//        if (itemInfo.isConsumable) {
//            putConsumable(parcel, itemInfo, data);
//        }
//    }

    @Override
    public void removeObject(MapObjectModel mapObjectModel) {
        if (mapObjectModel.isResource() && mapObjectModel instanceof ResMo) {
            removeStructure((StructureModel) mapObjectModel);
        }
    }

    private ConsumableModel putConsumable(ParcelModel parcel, ItemInfo itemInfo, int quantity) {
        ConsumableModel consumable = null;
        if (parcel != null && quantity > 0) {
            final ParcelModel finalParcel = WorldHelper.getNearestFreeArea(parcel, itemInfo, quantity);
            if (finalParcel != null) {
                if (finalParcel.getConsumable() != null) {
                    consumable = finalParcel.getConsumable();
                    consumable.addQuantity(quantity);
                } else {
                    consumable = new ConsumableModel(itemInfo);
                    consumable.setQuantity(quantity);
                    moveConsumableToParcel(finalParcel, consumable);
                    _consumables.add(finalParcel.getConsumable());
                }

                notifyObservers(observer -> observer.onAddConsumable(finalParcel, finalParcel.getConsumable()));
            }
        }
        return consumable;
    }
}
