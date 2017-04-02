package org.smallbox.faraway.modules.consumable;

import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.GameException;
import org.smallbox.faraway.core.dependencyInjector.BindComponent;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.ModuleSerializer;
import org.smallbox.faraway.core.module.path.PathManager;
import org.smallbox.faraway.core.module.world.model.*;
import org.smallbox.faraway.modules.character.model.PathModel;
import org.smallbox.faraway.modules.job.JobModel;
import org.smallbox.faraway.modules.job.JobModule;
import org.smallbox.faraway.modules.structure.StructureModule;
import org.smallbox.faraway.modules.structure.StructureModuleObserver;
import org.smallbox.faraway.modules.world.WorldModule;
import org.smallbox.faraway.util.Log;
import org.smallbox.faraway.util.Utils;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Alex on 26/06/2015.
 */
@ModuleSerializer(ConsumableSerializer.class)
public class ConsumableModule extends GameModule<ConsumableModuleObserver> {

    @BindComponent
    private PathManager pathManager;

    @BindComponent
    private Data data;

    @BindModule
    private WorldModule worldModule;

    @BindModule
    private JobModule jobModule;

    @BindModule
    private StructureModule structureModule;

    private Collection<ConsumableItem> _consumables;

    public void addConsumable(String itemName, int quantity, int x, int y, int z) {
        addConsumable(data.getItemInfo(itemName), quantity, x, y, z);
    }

    public void addConsumable(String itemName, int quantity, ParcelModel parcel) {
        addConsumable(data.getItemInfo(itemName), quantity, parcel);
    }

    public void addConsumable(ItemInfo itemInfo, int[] quantity, int x, int y, int z) {
        addConsumable(itemInfo, Utils.getRandom(quantity), x, y, z);
    }

    public void addConsumable(ItemInfo itemInfo, int[] quantity, ParcelModel parcel) {
        addConsumable(itemInfo, Utils.getRandom(quantity), parcel);
    }

    public void addConsumable(ItemInfo itemInfo, int quantity, int x, int y, int z) {
        addConsumable(itemInfo, quantity, WorldHelper.getParcel(x, y, z));
    }

    // TODO à clean et tester
    public void addConsumable(ItemInfo itemInfo, int quantity, ParcelModel targetParcel) {

        if (quantity < 0) {
            throw new GameException(ConsumableModule.class, "addConsumable: invalid quantity (%d)", quantity);
        }

        ParcelModel finalParcel = WorldHelper.move(targetParcel, parcel -> {
            ConsumableItem consumable = getConsumable(parcel);

            // La parcel ne contient pas le bon consomable
            if (consumable != null && consumable.getInfo() != itemInfo) {
                return false;
            }

            // La parcel contient le bon consomable mais il ne peux pas accepter la quantité à ajouter
            return !(consumable != null && consumable.getInfo() == itemInfo && consumable.getFreeQuantity() + quantity > consumable.getInfo().stack);
        });

        // Ajout du consomable à la parcel
        if (finalParcel != null) {
            ConsumableItem consumable = getConsumable(finalParcel);

            // Ajout de la quantité à un consomable déjà existant
            if (consumable != null) {
                consumable.addQuantity(quantity);
            }

            // Ajout d'un nouveau consomable
            else {
                consumable = new ConsumableItem(itemInfo, quantity);
                consumable.setParcel(finalParcel);
                _consumables.add(consumable);
            }
        }

        // Aucune parcel n'a pu être trouvée
        else {
            Log.warning(ConsumableModule.class, "No parcel found to add consumable (item: %s parcel: %s)", itemInfo, targetParcel);
        }
    }

    public Collection<ConsumableJobLock> getLocks() {
        return _locks;
    }

    public void cancelLock(ConsumableJobLock lock) {
        Log.debug(ConsumableModule.class, "CancelLock (lock: %s)", lock);

        if (lock.available && _locks.remove(lock)) {
            lock.consumable.removeLock(lock);
            lock.consumable.addQuantity(lock.quantity);
        }
    }

    public void cancelLock(JobModel job) {
        Log.debug(ConsumableModule.class, "CancelLock (job: %s)", job);

        _locks.stream()
                .filter(lock -> lock.available && lock.job == job)
                .forEach(lock -> {
                    lock.available = false;
                    lock.consumable.removeLock(lock);
                    lock.consumable.addQuantity(lock.quantity);
                });

        _locks.removeIf(lock -> !lock.available);
    }

    public boolean parcelAcceptConsumable(ParcelModel parcel, ConsumableItem consumable) {
        ConsumableItem consumableOnTargetParcel = getConsumable(parcel);
        if (consumableOnTargetParcel == null) {
            return true;
        }
        return consumableOnTargetParcel.getInfo() == consumable.getInfo() && consumableOnTargetParcel.getTotalQuantity() + consumable.getFreeQuantity() < consumable.getInfo().stack;
    }

    public boolean parcelAcceptConsumable(ParcelModel parcel, ItemInfo itemInfo, int quantity) {
        ConsumableItem consumableOnTargetParcel = getConsumable(parcel);
        if (consumableOnTargetParcel == null) {
            return true;
        }
        return consumableOnTargetParcel.getInfo() == itemInfo && consumableOnTargetParcel.getTotalQuantity() + quantity < itemInfo.stack;
    }

    /**
     * Crée un ConsumeJob
     *
     * @param consumable
     * @param totalDuration
     * @param callback
     * @return Le job créé
     */
    public ConsumeJob createConsumeJob(ConsumableItem consumable, double totalDuration, ConsumeJob.OnConsumeCallback callback) {
        return jobModule.createJob(new ConsumeJob(this, consumable, totalDuration, callback));
    }

    public boolean createHaulToFactoryJobs(MapObjectModel item, ItemInfo itemInfo, int quantity) {

        // Compte le nombre de consomables qui seront rapportés par les jobs existants
        int quantityInJob = jobModule.getJobs().stream()
                .filter(job -> job instanceof BasicHaulJob)
                .map(job -> (BasicHaulJob)job)
                .filter(job -> job.getItem() == item)
                .mapToInt(BasicHaulJob::getHaulingQuantity)
                .sum();

        // Ajoute des jobs tant que la quantité de consomable présent dans l'usine et les jobs est inférieur à la quantité requise
        int currentQuantity = 0;
        while (currentQuantity + quantityInJob < quantity) {
            BasicHaulJob job = createHaulToFactoryJob(itemInfo, item, quantity - (currentQuantity + quantityInJob));

            // Ajoute la quantity de consomable ammené par ce nouveau job à la quantity existante
            if (job != null) {
                Log.info("[Factory] %s -> launch hauling job for component: %s", item, itemInfo);

                quantityInJob += job.getHaulingQuantity();
            }

            else {
                Log.debug("[Factory] %s -> not enough component: %s", item, itemInfo);

                return false;
            }
        }

        return true;
    }

    public static class ConsumableJobLock {
        public ConsumableItem consumable;
        public JobModel job;
        public int quantity;
        public boolean available;

        public ConsumableJobLock(ConsumableItem consumable, JobModel job, int quantity) {
            this.consumable = consumable;
            this.job = job;
            this.quantity = quantity;
            this.available = true;
        }

        @Override
        public String toString() {
            return job + " " + consumable;
        }
    }

    public static class ParcelJobLock {
        public ConsumableItem consumable;
        public JobModel job;
        public int quantity;
        public boolean available;

        public ParcelJobLock(ConsumableItem consumable, JobModel job, int quantity) {
            this.consumable = consumable;
            this.job = job;
            this.quantity = quantity;
            this.available = true;
        }

        @Override
        public String toString() {
            return job + " " + consumable;
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
    protected void onModuleUpdate(Game game) {
        _consumables.forEach(ConsumableItem::fixPosition);

        // Retire les consomables ayant comme quantité 0
        _consumables.removeIf(consumable -> consumable.getTotalQuantity() == 0 && !consumable.hasLock());

        // Retire les locks des jobs n'existant plus
        _locks.stream()
                .filter(lock -> lock.job.getStatus() != JobModel.JobStatus.JOB_INITIALIZED && !jobModule.hasJob(lock.job))
                .forEach(this::unlock);
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
                PathModel path = pathManager.getPath(fromParcel, mapObject.getParcel(), false, false);
                if (path != null) {
                    ObjectsMatchingFilter.put(mapObject, path.getLength());
                    if (bestDistance > path.getLength()) {
                        bestDistance = path.getLength();
                    }
                }
            }
        }
        // Take first item at acceptable distance
        for (Map.Entry<MapObjectModel, Integer> entry : ObjectsMatchingFilter.entrySet()) {
            if (entry.getValue() <= bestDistance + Application.config.game.maxNearDistance) {
                return entry.getKey();
            }
        }

        return null;
    }

    public ConsumableItem createConsumableFromLock(ConsumableJobLock lock) {

        Log.debug(ConsumableModule.class, "TakeConsumable: (lock: %s)", lock);

        if (lock == null) {
            throw new GameException(ConsumableModule.class, "TakeConsumable: no lock for this job / consumable");
        }

        lock.available = false;
        lock.consumable.removeLock(lock);
        _locks.remove(lock);

        return new ConsumableItem(lock.consumable.getInfo(), lock.quantity);
    }

    /**
     * Crée un consomable depuis un lock existant (trouve le lock depuis le consomable d'origine)
     *
     * @param consumable Consomable d'origine
     * @return Consomable nouvellement créé
     */
    public ConsumableItem createConsumableFromLock(JobModel job, ConsumableItem consumable) {

        Log.debug(ConsumableModule.class, "TakeConsumable: (consumable: %s)", consumable);

        ConsumableJobLock targetLock = _locks.stream()
                .filter(lock -> lock.job == job && lock.consumable == consumable)
                .findFirst()
                .orElse(null);

        if (targetLock == null) {
            throw new GameException(ConsumableModule.class, "TakeConsumable: no lock for this job / consumable");
        }

        targetLock.available = false;
        targetLock.consumable.removeLock(targetLock);
        _locks.remove(targetLock);

        return new ConsumableItem(targetLock.consumable.getInfo(), targetLock.quantity);
    }

    public void removeConsumable(ConsumableItem consumable) {

        Log.debug(ConsumableModule.class, "RemoveConsumable: %s", consumable);

        if (consumable != null && consumable.getParcel() != null) {
            ParcelModel parcel = consumable.getParcel();
            _consumables.remove(consumable);

            notifyObservers(observer -> observer.onRemoveConsumable(parcel, consumable));
        }

    }

    public ConsumableItem putConsumable(ParcelModel parcel, ConsumableItem consumable) {
        if (parcel != null) {
            ParcelModel finalParcel = WorldHelper.getNearestFreeArea(parcel, consumable.getInfo(), consumable.getFreeQuantity());
            if (finalParcel == null) {
                return null;
            }

            // Ajout la quantity au consomable déjà présent
            ConsumableItem existingConsumable = finalParcel.getItem(ConsumableItem.class);
            if (existingConsumable != null) {
                existingConsumable.addQuantity(consumable.getFreeQuantity());
            }

            // Ajout le nouveau consomable sur la carte
            else {
                moveConsumableToParcel(finalParcel, consumable);
                _consumables.add(consumable);
            }

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
        throw new RuntimeException("this method is deprecated");
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

    public int getTotal(String itemName) {
        return getTotal(data.getItemInfo(itemName));
    }

    public int getTotal(ItemInfo itemInfo) {
        return _consumables.stream()
                .filter(consumable -> consumable.getInfo().instanceOf(itemInfo))
                .mapToInt(ConsumableItem::getFreeQuantity)
                .sum();
    }

    // TODO
    public int getTotalAccessible(ItemInfo itemInfo, ParcelModel parcel) {
        return _consumables.stream()
                .filter(consumable -> consumable.getInfo() == itemInfo)
                .mapToInt(ConsumableItem::getFreeQuantity)
                .sum();
    }

    // TODO
    public BasicHaulJob createHaulToFactoryJob(ItemInfo itemInfo, MapObjectModel item, int needQuantity) {
        HashMap<ConsumableItem, Integer> previewConsumables = new HashMap<>();
        int previewQuantity = 0;

        for (ConsumableItem consumable : _consumables) {
            if (consumable.getInfo().instanceOf(itemInfo)) {

                // Ajoute à la liste des preview le consomable et la quantité disponible
                int jobQuantity = Math.min(needQuantity - previewQuantity, consumable.getFreeQuantity());
                if (jobQuantity > 0) {
                    previewConsumables.put(consumable, jobQuantity);
                    previewQuantity += jobQuantity;
                }

            }
        }

        // Si suffisament de composants sont disponible alors le job est créé
        if (previewQuantity == needQuantity) {
            return BasicHaulJob.toFactory(this, jobModule, previewConsumables, item);
        }

        return null;
    }

    // TODO: perfs
    public ConsumableItem getConsumable(ParcelModel parcel) {
        return _consumables.stream().filter(consumableItem -> consumableItem.getParcel() == parcel).findFirst().orElse(null);
    }

    public ConsumableItem getConsumable(int x, int y, int z) {
        return getConsumable(WorldHelper.getParcel(x, y, z));
    }

    /**
     * Reserve une certaine quantité sur un consomable
     *
     * @param job
     * @param consumable
     * @param quantity
     * @return
     */
    public ConsumableJobLock lock(JobModel job, ConsumableItem consumable, int quantity) {

        Log.info(ConsumableModule.class, "Lock (job: %s, consumable: %s, quantity: %d)", job, consumable, quantity);

        if (_locks.stream().anyMatch(lock -> lock.job == job && lock.consumable == consumable)) {
            throw new GameException(ConsumableModule.class, "Un lock existe déjà pour ce job et ce consumable", consumable, quantity, _locks);
        }

        // Retourne false si le consomable n'a pas la quantité demandé de libre
        if (consumable.getFreeQuantity() == 0 || consumable.getFreeQuantity() < quantity) {
            Log.warning(ConsumableModule.class, "Not enough quantity to lock", consumable, quantity, _locks);
            return null;
        }

        // Retire la quantité demandée du consomable
        consumable.removeQuantity(quantity);

        // Ajoute le lock pour le consomable
        ConsumableJobLock lock = new ConsumableJobLock(consumable, job, quantity);

        consumable.addLock(lock);
        _locks.add(lock);

        Log.debug(ConsumableModule.class, "Lock ok (job: %s, consumable: %s, quantity: %d)", job, consumable, quantity);

        return lock;
    }

    private void unlock(ConsumableJobLock lock) {
        Log.debug(ConsumableModule.class, "Unlock (lock: %s)", lock);
        lock.consumable.removeLock(lock);
        _locks.remove(lock);
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
