package org.smallbox.faraway.module.consumable;

import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.BindModule;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.game.BindLuaController;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.game.module.character.model.PathModel;
import org.smallbox.faraway.core.game.module.job.model.HaulJob;
import org.smallbox.faraway.core.game.module.path.PathManager;
import org.smallbox.faraway.module.world.WorldInteractionModule;
import org.smallbox.faraway.module.world.WorldInteractionModuleObserver;
import org.smallbox.faraway.module.world.WorldModuleObserver;
import org.smallbox.faraway.core.game.module.world.model.*;
import org.smallbox.faraway.module.job.JobModule;
import org.smallbox.faraway.module.structure.StructureModule;
import org.smallbox.faraway.module.structure.StructureModuleObserver;
import org.smallbox.faraway.module.world.WorldModule;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Alex on 26/06/2015.
 */
public class ConsumableModule extends GameModule<ConsumableModuleObserver> {
    @BindModule("base.module.world")
    private WorldModule _world;

    @BindModule("base.module.jobs")
    private JobModule _jobs;

    @BindModule("base.module.structure")
    private StructureModule _structureModel;

    @BindModule("")
    private WorldInteractionModule _worldInteraction;

    @BindLuaController
    private ConsumableInfoController _infoController;

    private Collection<ConsumableModel> _consumables;

    public Collection<ConsumableModel> getConsumables() {
        return _consumables;
    }

    @Override
    protected void onGameCreate(Game game) {
        _consumables = new LinkedBlockingQueue<>();

        _structureModel.addObserver(new StructureModuleObserver() {
            @Override
            public void onStructureComplete(StructureModel structure) {
                if (!structure.isWalkable() && structure.getParcel().hasConsumable()) {
                    moveConsumableToParcel(WorldHelper.getNearestFreeParcel(structure.getParcel(), true, true), structure.getParcel().getConsumable());
                }
            }
        });

        _worldInteraction.addObserver(new WorldInteractionModuleObserver() {
            private ConsumableModel _lastConsumable;
            private ConsumableModel _currentConsumable;

            @Override
            public void onSelect(Collection<ParcelModel> parcels) {
                _currentConsumable = null;
                _consumables.stream()
                        .filter(consumable -> parcels.contains(consumable.getParcel()))
                        .forEach(consumable -> {
                            _currentConsumable = consumable;
                            notifyObservers(obs -> obs.onSelectConsumable(consumable));
                        });

                if (_lastConsumable != null && _currentConsumable == null) {
                    notifyObservers(obs -> obs.onDeselectConsumable(_lastConsumable));
                }

                _lastConsumable = _currentConsumable;
            }
        });

        _world.addObserver(new WorldModuleObserver() {
            @Override
            public void onAddParcel(ParcelModel parcel) {
                if (parcel.hasConsumable()) {
                    _consumables.add(parcel.getConsumable());
                }
            }
        });
    }

    @Override
    protected void onGameUpdate(Game game, int tick) {
        _consumables.forEach(ConsumableModel::fixPosition);
        _consumables.stream()
                .filter(consumable -> consumable.getQuantity() == 0 && consumable.getParcel() != null)
                .forEach(consumable -> consumable.getParcel().setConsumable(null));
        _consumables.removeIf(consumable -> consumable.getQuantity() == 0);
    }

    public MapObjectModel getRandomNearest(ItemFilter filter, ParcelModel fromParcel) {
        List<? extends MapObjectModel> list = new ArrayList<>(_consumables);

        // Get matching items
        int start = (int) (Math.random() * list.size());
        int length = list.size();
        int bestDistance = Integer.MAX_VALUE;
        Map<MapObjectModel, Integer> ObjectsMatchingFilter = new HashMap<>();
        for (int i = 0; i < length; i++) {
            MapObjectModel mapObject = list.get((i + start) % length);
            if (mapObject.matchFilter(filter)) {
                PathModel path = PathManager.getInstance().getPath(fromParcel, mapObject.getParcel(), false, false);
                if (path != null) {
                    ObjectsMatchingFilter.put(mapObject, path.getLength());
                    if (bestDistance > path.getLength()) {
                        bestDistance = path.getLength();
                    }
                }
            }
        }
        // Take first item at acceptable distance
        for (Map.Entry<MapObjectModel, Integer> entry: ObjectsMatchingFilter.entrySet()) {
            if (entry.getValue() <= bestDistance + Application.getInstance().getConfig().game.maxNearDistance) {
                return entry.getKey();
            }
        }

        return null;
    }


    public void removeConsumable(ConsumableModel consumable) {

        // TODO
//        if (consumable != null) {
//            if (_consumables.contains(consumable)) {
//                _consumables.remove(consumable);
//
//                consumable.removeJob(this);
//                consumable.setStoreJob(null);
//                if (consumable.getLock() == this) {
//                    consumable.lock(null);
//                }
//            }
//        }

        if (consumable != null && consumable.getParcel() != null) {
            ParcelModel parcel = consumable.getParcel();
            if (consumable.getParcel().getConsumable() == consumable) {
                consumable.getParcel().setConsumable(null);
            }
            _consumables.remove(consumable);

            _jobs.getJobs().stream()
                    .filter(job -> job instanceof HaulJob)
                    .forEach(job -> ((HaulJob) job).removePotentialConsumable(consumable));

            notifyObservers(observer -> observer.onRemoveConsumable(parcel, consumable));
        }
    }

    public ConsumableModel putConsumable(ParcelModel parcel, ConsumableModel consumable) {
        if (parcel != null) {
            ParcelModel finalParcel = WorldHelper.getNearestFreeArea(parcel, consumable.getInfo(), consumable.getQuantity());
            if (finalParcel == null) {
                return null;
            }

            // Put consumable on free org.smallbox.faraway.core.game.module.room.model
            if (finalParcel.getConsumable() != null) {
                finalParcel.getConsumable().addQuantity(consumable.getQuantity());
            } else {
                moveConsumableToParcel(finalParcel, consumable);
                _consumables.add(finalParcel.getConsumable());
            }

            _jobs.getJobs().stream()
                    .filter(job -> job instanceof HaulJob)
                    .forEach(job -> ((HaulJob) job).addPotentialConsumable(consumable));

            notifyObservers(observer -> observer.onAddConsumable(finalParcel, consumable));

            return consumable;
        }

        return null;
    }

    private void moveConsumableToParcel(ParcelModel parcel, ConsumableModel consumable) {
        parcel.setConsumable(consumable);
        if (consumable != null && consumable.getParcel() != null) {
            consumable.getParcel().setConsumable(null);
        }
        if (consumable != null) {
            consumable.setParcel(parcel);

            if (parcel.getConsumable() == null) {
                parcel.setConsumable(consumable);
            }
        }
    }

//    @Override
//    public void onItemComplete(ItemModel item) {
//        if (item.getParcel().hasConsumable()) {
//            moveConsumableToParcel(WorldHelper.getNearestFreeParcel(item.getParcel(), true, true), item.getParcel().getConsumable());
//        }
//    }

    @Override
    public void putObject(ParcelModel parcel, ItemInfo itemInfo, int data, boolean complete) {
        if (itemInfo.isConsumable) {
            putConsumable(parcel, itemInfo, data);
        }
    }

    @Override
    public void removeObject(MapObjectModel mapObjectModel) {
        if (mapObjectModel.isConsumable() && mapObjectModel instanceof ConsumableModel) {
            removeConsumable((ConsumableModel) mapObjectModel);
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
