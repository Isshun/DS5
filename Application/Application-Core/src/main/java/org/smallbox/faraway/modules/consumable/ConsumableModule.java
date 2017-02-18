package org.smallbox.faraway.modules.consumable;

import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.ModuleSerializer;
import org.smallbox.faraway.core.module.character.model.PathModel;
import org.smallbox.faraway.core.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.module.world.model.*;
import org.smallbox.faraway.modules.item.UsableItem;
import org.smallbox.faraway.modules.job.JobModule;
import org.smallbox.faraway.modules.structure.StructureModule;
import org.smallbox.faraway.modules.structure.StructureModuleObserver;
import org.smallbox.faraway.modules.world.WorldModule;
import org.smallbox.faraway.util.Log;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Alex on 26/06/2015.
 */
@ModuleSerializer(ConsumableSerializer.class)
//@ModuleRenderer(ConsumableRenderer.class)
public class ConsumableModule extends GameModule<ConsumableModuleObserver> {

    @BindModule
    private WorldModule worldModule;

    @BindModule
    private JobModule jobModule;

    @BindModule
    private StructureModule structureModule;

//    @BindModule
//    private WorldInteractionModule worldInteractionModule;

    private Collection<ConsumableItem> _consumables;

    private class ConsumableJobLock {
        public ConsumableItem consumable;
        public JobModel job;
        public int quantity;

        public ConsumableJobLock(ConsumableItem consumable, JobModel job, int quantity) {
            this.consumable = consumable;
            this.job = job;
            this.quantity = quantity;
        }
    }

    private Collection<ConsumableJobLock> _locks = new ConcurrentLinkedQueue<>();

    public Collection<ConsumableItem> getConsumables() {
        return _consumables;
    }

    @Override
    public void onGameCreate(Game game) {
        _consumables = new LinkedBlockingQueue<>();

        structureModule.addObserver(new StructureModuleObserver() {
            @Override
            public void onStructureComplete(StructureItem structure) {
                if (!structure.isWalkable() && hasConsumable(structure.getParcel())) {
                    moveConsumableToParcel(WorldHelper.getNearestFreeParcel(structure.getParcel(), true, true), structure.getParcel().getItem(ConsumableItem.class));
                }
            }
        });

//        worldInteractionModule.addObserver(new WorldInteractionModuleObserver() {
//            private ConsumableItem _lastConsumable;
//            private ConsumableItem _currentConsumable;
//
//            @Override
//            public void onSelect(GameEvent event, Collection<ParcelModel> parcels) {
//                _currentConsumable = null;
//                _consumables.stream()
//                        .filter(consumable -> parcels.contains(consumable.getParcel()))
//                        .forEach(consumable -> {
//                            _currentConsumable = consumable;
//                            notifyObservers(obs -> obs.onSelectConsumable(consumable));
//                        });
//
//                if (_lastConsumable != null && _currentConsumable == null) {
//                    notifyObservers(obs -> obs.onDeselectConsumable(_lastConsumable));
//                }
//
//                _lastConsumable = _currentConsumable;
//            }
//        });
    }

    private boolean hasConsumable(ParcelModel parcel) {
        return getConsumable(parcel) != null;
    }

    @Override
    protected void onGameUpdate(Game game, int tick) {
        _consumables.forEach(ConsumableItem::fixPosition);
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
                PathModel path = Application.pathManager.getPath(fromParcel, mapObject.getParcel(), false, false);
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
            if (entry.getValue() <= bestDistance + Application.APPLICATION_CONFIG.game.maxNearDistance) {
                return entry.getKey();
            }
        }

        return null;
    }


    public void removeConsumable(ConsumableItem consumable) {

        // TODO
//        if (consumable != null) {
//            if (_consumables.contains(consumable)) {
//                _consumables.remove(consumable);
//
//                consumable.removeJob(this);
//                consumable.setStoreJob(null);
//                if (consumable.getJob() == this) {
//                    consumable.setJob(null);
//                }
//            }
//        }

        if (consumable != null && consumable.getParcel() != null) {
            ParcelModel parcel = consumable.getParcel();
            _consumables.remove(consumable);

            jobModule.getJobs().stream()
                    .filter(job -> job instanceof HaulJob)
                    .forEach(job -> ((HaulJob) job).removePotentialConsumable(consumable));

            notifyObservers(observer -> observer.onRemoveConsumable(parcel, consumable));
        }
    }

    public ConsumableItem putConsumable(ParcelModel parcel, ConsumableItem consumable) {
        if (parcel != null) {
            ParcelModel finalParcel = WorldHelper.getNearestFreeArea(parcel, consumable.getInfo(), consumable.getQuantity());
            if (finalParcel == null) {
                return null;
            }

            // Ajout la quantity au consomable déjà présent
            ConsumableItem existingConsumable = finalParcel.getItem(ConsumableItem.class);
            if (existingConsumable != null) {
                existingConsumable.addQuantity(consumable.getQuantity());
            }

            // Ajout le nouveau consomable sur la carte
            else {
                moveConsumableToParcel(finalParcel, consumable);
                _consumables.add(consumable);
            }

            jobModule.getJobs().stream()
                    .filter(job -> job instanceof HaulJob)
                    .forEach(job -> ((HaulJob) job).addPotentialConsumable(consumable));

            notifyObservers(observer -> observer.onAddConsumable(finalParcel, consumable));

            return consumable;
        }

        return null;
    }

    private void moveConsumableToParcel(ParcelModel parcel, ConsumableItem consumable) {
        if (consumable != null) {
            if (consumable.getParcel() != null) {
                consumable.getParcel().setItem(null);
            }
            consumable.setParcel(parcel);
            parcel.setItem(consumable);
        }
    }

    @Override
    public void putObject(ParcelModel parcel, ItemInfo itemInfo, int data, boolean complete) {
        if (itemInfo.isConsumable) {
            putConsumable(parcel, itemInfo, data);
        }
    }

    @Override
    public void removeObject(MapObjectModel mapObjectModel) {
        if (mapObjectModel.isConsumable() && mapObjectModel instanceof ConsumableItem) {
            removeConsumable((ConsumableItem) mapObjectModel);
        }
    }

    public ConsumableItem putConsumable(ParcelModel parcel, ItemInfo itemInfo, int quantity) {
        assert parcel != null;

        if (quantity > 0) {
            final ParcelModel finalParcel = WorldHelper.getNearestFreeArea(parcel, itemInfo, quantity);
            if (finalParcel != null) {
                // Ajoute la quantité au consomable déjà présent
                ConsumableItem existingConsumable = finalParcel.getItem(ConsumableItem.class);
                if (existingConsumable != null) {
                    Log.info("ConsumableModule", "Add %d to %s at %s", quantity, itemInfo, finalParcel);
                    existingConsumable.addQuantity(quantity);
                    notifyObservers(observer -> observer.onUpdateQuantity(finalParcel, existingConsumable, existingConsumable.getQuantity() - quantity, existingConsumable.getQuantity()));
                    return existingConsumable;
                }

                // Crée un nouveau consomable
                else {
                    Log.info("ConsumableModule", "Create %s x %d at %s", itemInfo, quantity, finalParcel);
                    ConsumableItem consumable = new ConsumableItem(itemInfo);
                    consumable.setQuantity(quantity);
                    moveConsumableToParcel(finalParcel, consumable);
                    _consumables.add(consumable);
                    notifyObservers(observer -> observer.onAddConsumable(finalParcel, consumable));
                    return consumable;
                }
            }
        }

        return null;
    }

    public ConsumableItem find(ItemInfo itemInfo) {
        return _consumables.stream()
                .filter(consumable -> consumable.getInfo() == itemInfo && consumable.getParcel() != null)
                .findAny().orElse(null);
    }

    public ConsumableItem create(ItemInfo info, int quantity, ParcelModel parcel) {
        ConsumableItem consumable = new ConsumableItem(info);
        consumable.setQuantity(quantity);
        consumable.setParcel(parcel);

        _consumables.add(consumable);

        return consumable;
    }

    public void create(ItemInfo itemInfo, int quantity, int x, int y, int z) {
        ParcelModel parcel = worldModule.getParcel(x, y, z);
        if (parcel != null) {
            create(itemInfo, quantity, parcel);
        }
    }

    public int getTotal(ItemInfo itemInfo) {
        return _consumables.stream()
                .filter(consumable -> consumable.getInfo() == itemInfo)
                .mapToInt(ConsumableItem::getQuantity)
                .sum();
    }

    // TODO
    public int getTotalAccessible(ItemInfo itemInfo, ParcelModel parcel) {
        return _consumables.stream()
                .filter(consumable -> consumable.getInfo() == itemInfo)
                .mapToInt(ConsumableItem::getQuantity)
                .sum();
    }

    // TODO
    public BasicHaulJob createHaulJob(ItemInfo itemInfo, UsableItem item, int needQuantity) {
        for (ConsumableItem consumable: _consumables) {
            if (consumable.getInfo() == itemInfo) {

                // Calcul le nombre d'élément de la pile déjà consomés par des jobs
                int quantityInJob = 0;
                for (JobModel job: jobModule.getJobs()) {
                    if (job instanceof HaulJob && ((HaulJob)job).getConsumable() == consumable) {
                        quantityInJob += ((HaulJob)job).getRealQuantity();
                    }
                }

                // Si toute la pile n'est pas déjà consomée crée un nouveau HaulJob
                if (quantityInJob < consumable.getQuantity()) {
                    return BasicHaulJob.toFactory(consumable, item, Math.min(needQuantity, consumable.getQuantity() - quantityInJob));
                }

            }
        }

        return null;
    }

    // TODO: perfs
    public ConsumableItem getConsumable(ParcelModel parcel) { return _consumables.stream().filter(consumableItem -> consumableItem.getParcel() == parcel).findFirst().orElse(null); }

    /**
     * Reserve une certaine quantité sur un consomable
     *
     * @param job
     * @param consumable
     * @param quantity
     * @return
     */
    public boolean lock(JobModel job, ConsumableItem consumable, int quantity) {

        // Retourne false si le consomable n'a pas la quantité demandé de libre
        if (consumable.getQuantity() - _locks.stream()
                .filter(lock -> lock.consumable == consumable)
                .mapToInt(lock -> lock.quantity)
                .sum() < quantity) {
            return false;
        }

        // Ajoute le lock pour le consomable
        _locks.add(new ConsumableJobLock(consumable, job, quantity));

        return true;
    }

    /**
     * Retire la réservation créé par la methode lock
     *
     * @param job
     * @param consumable
     * @return true si la réservation existe
     */
    public boolean unlock(JobModel job, ConsumableItem consumable) {
        return _locks.removeIf(lock -> lock.job == job && lock.consumable == consumable);
    }

    /**
     * Retourne true si le composant apparait dans la liste des objets ayant une réservation
     *
     * @param consumable
     * @return
     */
    public boolean hasLock(ConsumableItem consumable) {
        return _locks.stream().anyMatch(lock -> lock.consumable == consumable);
    }

}
