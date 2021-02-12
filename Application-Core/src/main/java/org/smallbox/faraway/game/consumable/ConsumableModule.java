package org.smallbox.faraway.game.consumable;

import org.smallbox.faraway.util.GameException;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.module.SuperGameModule;
import org.smallbox.faraway.core.game.DataManager;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.game.world.WorldHelper;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.config.ApplicationConfig;
import org.smallbox.faraway.core.path.PathManager;
import org.smallbox.faraway.core.world.model.ItemFilter;
import org.smallbox.faraway.core.world.model.MapObjectModel;
import org.smallbox.faraway.game.world.Parcel;
import org.smallbox.faraway.game.character.model.PathModel;
import org.smallbox.faraway.game.job.JobModel;
import org.smallbox.faraway.game.job.JobModule;
import org.smallbox.faraway.game.job.JobStatus;
import org.smallbox.faraway.game.structure.StructureModule;
import org.smallbox.faraway.game.world.WorldModule;
import org.smallbox.faraway.util.Random;
import org.smallbox.faraway.util.log.Log;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

@GameObject
public class ConsumableModule extends SuperGameModule<Consumable, ConsumableModuleObserver> {
    @Inject private PathManager pathManager;
    @Inject private DataManager dataManager;
    @Inject private WorldModule worldModule;
    @Inject private JobModule jobModule;
    @Inject private StructureModule structureModule;
    @Inject private ApplicationConfig applicationConfig;

    public void addConsumable(String itemName, int quantity, int x, int y, int z) {
        addConsumable(dataManager.getItemInfo(itemName), quantity, WorldHelper.getParcel(x, y, z), 0);
    }

    public void addConsumable(String itemName, int quantity, int x, int y, int z, int stack) {
        addConsumable(dataManager.getItemInfo(itemName), quantity, WorldHelper.getParcel(x, y, z), stack);
    }

    public void addConsumable(String itemName, int quantity, Parcel parcel) {
        addConsumable(dataManager.getItemInfo(itemName), quantity, parcel);
    }

    public void addConsumable(ItemInfo itemInfo, int[] quantity, int x, int y, int z) {
        addConsumable(itemInfo, Random.interval(quantity), x, y, z);
    }

    public void addConsumable(ItemInfo itemInfo, int[] quantity, Parcel parcel) {
        addConsumable(itemInfo, Random.interval(quantity), parcel);
    }

    public void addConsumable(ItemInfo itemInfo, int quantity, int x, int y, int z) {
        addConsumable(itemInfo, quantity, WorldHelper.getParcel(x, y, z));
    }

    // TODO à clean et tester
    public Consumable addConsumable(ItemInfo itemInfo, int quantity, Parcel parcel) {
        return addConsumable(itemInfo, quantity, parcel, 0);
    }

    public Consumable addConsumable(ItemInfo itemInfo, int quantity, Parcel targetParcel, int stack) {

        if (quantity < 0) {
            throw new GameException(ConsumableModule.class, "addConsumable: invalid quantity (%d)", quantity);
        }

        Parcel finalParcel = WorldHelper.move(targetParcel, parcel -> {
            Consumable consumable = getConsumable(parcel, stack);

            // La parcel ne contient pas le bon consomable
            if (consumable != null && consumable.getInfo() != itemInfo) {
                return false;
            }

            // La parcel contient le bon consomable mais il ne peux pas accepter la quantité à ajouter
            return !(consumable != null && consumable.getInfo() == itemInfo && consumable.getActualQuantity() + quantity > consumable.getInfo().stack);
        });

        // Ajout du consomable à la parcel
        if (finalParcel != null) {
            Consumable consumable = getConsumable(finalParcel, stack);

            // Ajout de la quantité à un consomable déjà existant
            if (consumable != null) {
                consumable.addQuantity(quantity);
            }

            // Ajout d'un nouveau consomable
            else {
                consumable = new Consumable(itemInfo, quantity, stack);
                consumable.setParcel(finalParcel);
                add(consumable);
            }

            return consumable;
        }

        // Aucune parcel n'a pu être trouvée
        else {
            Log.warning(ConsumableModule.class, "No parcel found to add consumable (item: %s parcel: %s)", itemInfo, targetParcel);
        }

        return null;
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

    public boolean parcelAcceptConsumable(Parcel parcel, Consumable consumable) {
        List<Consumable> consumablesOnTargetParcel = getConsumableList(parcel);
        if (consumablesOnTargetParcel.size() < 4) {
            return true;
        }

        int totalQuantity = consumablesOnTargetParcel.stream().filter(existingConsumable -> existingConsumable.getInfo() == consumable.getInfo()).mapToInt(Consumable::getTotalQuantity).sum();
        int actualQuantity = consumablesOnTargetParcel.stream().filter(existingConsumable -> existingConsumable.getInfo() == consumable.getInfo()).mapToInt(Consumable::getActualQuantity).sum();

        return consumable.getTotalQuantity() <= totalQuantity - actualQuantity;
    }

    public boolean parcelAcceptConsumable(Parcel parcel, ItemInfo itemInfo, int quantity) {
        Consumable consumableOnTargetParcel = getConsumable(parcel);
        if (consumableOnTargetParcel == null) {
            return true;
        }
        return consumableOnTargetParcel.getInfo() == itemInfo && consumableOnTargetParcel.getTotalQuantity() + quantity < itemInfo.stack;
    }

//    public boolean createHaulToFactoryJobs(MapObjectModel item, ItemInfo itemInfo, int quantity) {
//
//        // Compte le nombre de consomables qui seront rapportés par les jobs existants
//        int quantityInJob = jobModule.getJobs().stream()
//                .filter(job -> job instanceof BasicHaulJob)
//                .map(job -> (BasicHaulJob)job)
//                .filter(job -> job.getItem() == item)
//                .mapToInt(BasicHaulJob::getHaulingQuantity)
//                .sum();
//
//        // Ajoute des jobs tant que la quantité de consomable présent dans l'usine et les jobs est inférieur à la quantité requise
//        int currentQuantity = 0;
//        while (currentQuantity + quantityInJob < quantity) {
//            BasicHaulJob job = createHaulToFactoryJob(itemInfo, item, quantity - (currentQuantity + quantityInJob));
//
//            // Ajoute la quantity de consomable ammené par ce nouveau job à la quantity existante
//            if (job != null) {
//                Log.info("[Factory] %s -> launch hauling job for component: %s", item, itemInfo);
//
//                quantityInJob += job.getHaulingQuantity();
//            }
//
//            else {
//                Log.debug("[Factory] %s -> not enough component: %s", item, itemInfo);
//
//                return false;
//            }
//        }
//
//        return true;
//    }

    public void addToLock(JobModel job, Consumable consumable, int quantity) {

        // Le consomable n'a pas la quantité demandé de libre
        if (consumable.getActualQuantity() == 0 || consumable.getActualQuantity() < quantity) {
            Log.warning(ConsumableModule.class, "Not enough quantity to lock", consumable, quantity, _locks);
            return;
        }

        ConsumableJobLock lock = getLock(job, consumable);

        // Le lock n'existe pas
        if (lock == null) {
            Log.warning(ConsumableModule.class, "Lock doesn't exists", consumable, quantity, _locks);
            return;
        }

        // Retire la quantité demandée du consomable
        consumable.removeQuantity(quantity);

        // Ajoute la quantité au lock
        lock.quantity += quantity;
    }

    public int removeQuantity(Consumable consumable, int quantity) {
        if (quantity < consumable.getTotalQuantity()) {
            consumable.setQuantity(consumable.getTotalQuantity() - quantity);
            return quantity;
        }

        removeConsumable(consumable);
        return consumable.getTotalQuantity();
    }

    // TODO: handle null or full parcel
    public int addQuantity(Parcel parcel, int quantity, ItemInfo itemInfo) {
        Log.info("Add " + quantity + " " + itemInfo.label + " to " + parcel);
        int consumableOnParcel = 0;
        for (Consumable consumable: getAll()) {
            if (consumable.getParcel() == parcel) {
                consumableOnParcel++;
                if (consumable.getInfo() == itemInfo && quantity > 0) {
                    quantity = consumable.addQuantity(quantity);
                }
            }
        }

        while (consumableOnParcel < 4 && quantity > 0) {
            Consumable newConsumable = new Consumable(itemInfo, 0, getFreeGridPosition(parcel));
            newConsumable.setParcel(parcel);
            quantity = newConsumable.addQuantity(quantity);
            add(newConsumable);
            consumableOnParcel++;
        }

        return quantity;
    }

    private int getFreeGridPosition(Parcel parcel) {
        List<Integer> usedPositions = getAll().stream().filter(consumable -> consumable.getParcel() == parcel).map(Consumable::getGridPosition).collect(Collectors.toList());
        if (!usedPositions.contains(0)) return 0;
        if (!usedPositions.contains(1)) return 1;
        if (!usedPositions.contains(2)) return 2;
        if (!usedPositions.contains(3)) return 3;
        return -1;
    }

    public int getFreeSpace(Parcel parcel, ItemInfo itemInfo) {
        List<Consumable> consumables = getConsumablesOnParcel(parcel);
        return (4 - consumables.size()) * itemInfo.stack + consumables.stream().filter(consumable -> consumable.getInfo() == itemInfo).mapToInt(Consumable::getFreeSpace).sum();
    }

    public List<Consumable> getConsumablesOnParcel(Parcel parcel) {
        return getAll().stream().filter(consumableItem -> consumableItem.getParcel() == parcel).collect(Collectors.toList());
    }

    public static class ConsumableJobLock {
        public Consumable consumable;
        public JobModel job;
        public int quantity;
        public boolean available;

        public ConsumableJobLock(Consumable consumable, JobModel job, int quantity) {
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
        public Consumable consumable;
        public JobModel job;
        public int quantity;
        public boolean available;

        public ParcelJobLock(Consumable consumable, JobModel job, int quantity) {
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

    private final Collection<ConsumableJobLock> _locks = new ConcurrentLinkedQueue<>();

    @Override
    public void onGameCreate(Game game) {
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

    @Override
    public void onGameUpdate() {
        getAll().forEach(Consumable::fixPosition);

        // Retire les consomables ayant comme quantité 0
        getAll().removeIf(consumable -> consumable.getTotalQuantity() == 0 && !consumable.hasLock() && consumable.getStoreJob() == null);

        // Retire les locks des jobs n'existant plus
        _locks.stream()
                .filter(lock -> lock.job.getStatus() != JobStatus.JOB_INITIALIZED && !jobModule.hasJob(lock.job))
                .forEach(this::unlock);
    }

    public MapObjectModel getRandomNearest(ItemFilter filter, Parcel fromParcel) {
        List<? extends MapObjectModel> list = new ArrayList<>(getAll());

        // Get matching items
        int start = (int) (Math.random() * list.size());
        int length = list.size();
        int bestDistance = Integer.MAX_VALUE;
        Map<MapObjectModel, Integer> ObjectsMatchingFilter = new HashMap<>();
        for (int i = 0; i < length; i++) {
            MapObjectModel mapObject = list.get((i + start) % length);
            if (mapObject.matchFilter(filter)) {
                PathModel path = pathManager.getPath(fromParcel, mapObject.getParcel(), false, false, true);
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
            if (entry.getValue() <= bestDistance + applicationConfig.game.maxNearDistance) {
                return entry.getKey();
            }
        }

        return null;
    }

    public Consumable createConsumableFromLock(ConsumableJobLock lock) {

        Log.debug(ConsumableModule.class, "TakeConsumable: (lock: %s)", lock);

        if (lock == null) {
            throw new GameException(ConsumableModule.class, "TakeConsumable: no lock for this job / consumable");
        }

        lock.available = false;
        lock.consumable.removeLock(lock);
        _locks.remove(lock);

        return new Consumable(lock.consumable.getInfo(), lock.quantity);
    }

    /**
     * Crée un consomable depuis un lock existant (trouve le lock depuis le consomable d'origine)
     *
     * @param consumable Consomable d'origine
     * @return Consomable nouvellement créé
     */
    public Consumable createConsumableFromLock(JobModel job, Consumable consumable) {

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

        return new Consumable(targetLock.consumable.getInfo(), targetLock.quantity);
    }

    public int getLockQuantity(JobModel job, Consumable consumable) {
        ConsumableJobLock lock = getLock(job, consumable);
        return lock != null ? lock.quantity : 0;
    }

    public ConsumableJobLock getLock(JobModel job, Consumable consumable) {
        return _locks.stream()
                .filter(lock -> lock.job == job)
                .filter(lock -> lock.consumable == consumable)
                .findFirst()
                .orElse(null);
    }

    public void removeConsumable(Consumable consumable) {

        Log.debug(ConsumableModule.class, "RemoveConsumable: %s", consumable);

        if (consumable != null && consumable.getParcel() != null) {
            Parcel parcel = consumable.getParcel();
            remove(consumable);

            notifyObservers(observer -> observer.onRemoveConsumable(parcel, consumable));
        }

    }

    public Consumable putConsumable(Parcel parcel, Consumable consumable) {
        if (parcel != null) {
            Parcel finalParcel = WorldHelper.getNearestFreeArea(parcel, consumable.getInfo(), consumable.getActualQuantity());
            if (finalParcel == null) {
                return null;
            }

            // Ajout la quantity au consomable déjà présent
            Consumable existingConsumable = finalParcel.getItem(Consumable.class);
            if (existingConsumable != null) {
                existingConsumable.addQuantity(consumable.getActualQuantity());
            }

            // Ajout le nouveau consomable sur la carte
            else {
                moveConsumableToParcel(finalParcel, consumable);
                add(consumable);
            }

            notifyObservers(observer -> observer.onAddConsumable(finalParcel, consumable));

            return consumable;
        }

        return null;
    }

    private void moveConsumableToParcel(Parcel parcel, Consumable consumable) {
        if (consumable != null) {
            if (consumable.getParcel() != null) {
                consumable.getParcel().setItem(null);
            }
            consumable.setParcel(parcel);
            parcel.setItem(consumable);
        }
    }

    @Override
    public void putObject(Parcel parcel, ItemInfo itemInfo, int data, boolean complete) {
        if (itemInfo.isConsumable) {
            putConsumable(parcel, itemInfo, data);
        }
    }

    @Override
    public void removeObject(MapObjectModel mapObjectModel) {
        if (mapObjectModel.isConsumable() && mapObjectModel instanceof Consumable) {
            removeConsumable((Consumable) mapObjectModel);
        }
    }

    public Consumable putConsumable(Parcel parcel, ItemInfo itemInfo, int quantity) {
        throw new RuntimeException("this method is deprecated");
    }

    public Consumable find(ItemInfo itemInfo) {
        return getAll().stream()
                .filter(consumable -> consumable.getInfo() == itemInfo && consumable.getParcel() != null)
                .findAny().orElse(null);
    }

    public Consumable create(ItemInfo info, int quantity, Parcel parcel, int gridPosition) {
        Consumable consumable = new Consumable(info);
        consumable.setQuantity(quantity);
        consumable.setParcel(parcel);
        consumable.setGridPosition(gridPosition);

        add(consumable);

        return consumable;
    }

    public void create(ItemInfo itemInfo, int quantity, int x, int y, int z, int gridPosition) {
        Parcel parcel = worldModule.getParcel(x, y, z);
        if (parcel != null) {
            create(itemInfo, quantity, parcel, gridPosition);
        }
    }

    public int getTotal(String itemName) {
        return getTotal(dataManager.getItemInfo(itemName));
    }

    public int getTotal(ItemInfo itemInfo) {
        return getAll().stream()
                .filter(consumable -> consumable.getInfo().instanceOf(itemInfo))
                .mapToInt(Consumable::getActualQuantity)
                .sum();
    }

    // TODO
    public int getTotalAccessible(ItemInfo itemInfo, Parcel parcel) {
        return getAll().stream()
                .filter(consumable -> consumable.getInfo() == itemInfo)
                .mapToInt(Consumable::getActualQuantity)
                .sum();
    }
//
//    // TODO
//    public BasicHaulJob createHaulToFactoryJob(ItemInfo itemInfo, MapObjectModel item, int needQuantity) {
//        HashMap<ConsumableItem, Integer> previewConsumables = new HashMap<>();
//        int previewQuantity = 0;
//
//        for (ConsumableItem consumable : getAll()) {
//            if (consumable.getInfo().instanceOf(itemInfo)) {
//
//                // Ajoute à la liste des preview le consomable et la quantité disponible
//                int jobQuantity = Math.min(needQuantity - previewQuantity, consumable.getFreeQuantity());
//                if (jobQuantity > 0) {
//                    previewConsumables.put(consumable, jobQuantity);
//                    previewQuantity += jobQuantity;
//                }
//
//            }
//        }
//
//        // Si suffisament de composants sont disponible alors le job est créé
//        if (previewQuantity == needQuantity) {
//            return BasicHaulJob.toFactory(this, jobModule, previewConsumables, item);
//        }
//
//        return null;
//    }

    // TODO: perfs
    public Consumable getConsumable(Parcel parcel) {
        return getConsumable(parcel, 0);
    }

    public List<Consumable> getConsumableList(Parcel parcel) {
        return getAll().stream()
                .filter(consumableItem -> consumableItem.getParcel() == parcel)
                .collect(Collectors.toList());
    }

    public Consumable getConsumable(Parcel parcel, int gridPosition) {
        return getAll().stream()
                .filter(consumableItem -> consumableItem.getParcel() == parcel)
                .filter(consumableItem -> consumableItem.getGridPosition() == gridPosition)
                .findFirst().orElse(null);
    }

    public Consumable getConsumable(int x, int y, int z) {
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
    public ConsumableJobLock lock(JobModel job, Consumable consumable, int quantity) {

        Log.info(ConsumableModule.class, "Lock (job: %s, consumable: %s, quantity: %d)", job, consumable, quantity);

        if (_locks.stream().anyMatch(lock -> lock.job == job && lock.consumable == consumable)) {
            throw new GameException(ConsumableModule.class, "Un lock existe déjà pour ce job et ce consumable", consumable, quantity, _locks);
        }

        // Retourne false si le consomable n'a pas la quantité demandé de libre
        if (consumable.getActualQuantity() == 0 || consumable.getActualQuantity() < quantity) {
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
    public boolean hasLock(Consumable consumable) {
        return _locks.stream().anyMatch(lock -> lock.consumable == consumable);
    }

}
