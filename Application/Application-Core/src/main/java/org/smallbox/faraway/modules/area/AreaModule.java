package org.smallbox.faraway.modules.area;

import org.smallbox.faraway.core.GameException;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.module.ModuleSerializer;
import org.smallbox.faraway.core.module.area.model.AreaModel;
import org.smallbox.faraway.core.module.area.model.GardenAreaModel;
import org.smallbox.faraway.core.module.area.model.StorageAreaModel;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.consumable.ConsumableModule;
import org.smallbox.faraway.modules.job.JobModule;
import org.smallbox.faraway.modules.world.WorldModule;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

/**
 * Created by Alex on 13/06/2015.
 */
@ModuleSerializer(AreaSerializer.class)
public class AreaModule extends GameModule {

    @BindModule
    private ConsumableModule consumableModule;

    @BindModule
    private JobModule jobModule;

    @BindModule
    private WorldModule worldModule;

    private Collection<AreaModel> _areas = new LinkedBlockingQueue<>();
    private Collection<GardenAreaModel> _gardens = new LinkedBlockingQueue<>();
    private Collection<StorageAreaModel> _storageAreas = new LinkedBlockingQueue<>();
    private Collection<Class<? extends AreaModel>> _areaTypes = new LinkedBlockingQueue<>();

    public AreaModule() {
        _updateInterval = 10;
    }

    @Override
    public void onGameCreate(Game game) {

//        consumableModule.addObserver(new ConsumableModuleObserver() {
//            @Override
//            public void onAddConsumable(ParcelModel parcel, ConsumableItem consumable) {
//                if (consumable.getJob() == null) {
//                    storeConsumable(consumable);
//                }
//            }
//        });
    }

    public Collection<AreaModel> getAreas() {
        return _areas;
    }

    public void init(List<StorageAreaModel> storageAreas, List<GardenAreaModel> gardenAreas) {

    }

    public <T extends AreaModel> T createArea(Class<T> cls, List<ParcelModel> parcels) {

        try {
            T area = cls.getConstructor().newInstance();
            parcels.forEach(area::addParcel);
            _areas.add(area);
            return area;
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            throw new GameException(JobModule.class, e, "Unable to create area");
        }
    }

    public List<ParcelModel> getParcels(Class<? extends AreaModel> cls) {
        return _areas.stream()
                .filter(area -> area.getClass().isInstance(cls))
                .flatMap(area -> area.getParcels().stream())
                .collect(Collectors.toList());
    }

    public <T extends AreaModel> List<T> getAreas(Class<T> cls) {
        return _areas.stream()
                .filter(cls::isInstance)
                .map(area -> (T)area)
                .collect(Collectors.toList());
    }

    public void addAreaType(Class<? extends AreaModel> cls) {
        _areaTypes.add(cls);
    }

    public <T extends AreaModel> T addArea(Class<T> cls, Collection<ParcelModel> parcels) {
        try {
            T area = cls.getConstructor().newInstance();
            parcels.forEach(area::addParcel);
            _areas.add(area);
            return area;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Collection<Class<? extends AreaModel>> getAreaTypes() {
        return _areaTypes;
    }

//    @Override
//    protected void onGameUpdate(Game game, int tick) {
//        // Create store jobs
////        jobModule.stream().filter(job -> job instanceof JobHaul).forEach(job -> ((JobHaul)job).foundConsumablesAround());
//        consumableModule.getConsumables().stream()
//                .filter(consumable -> consumable.getJob() == null)
//                .forEach(this::storeConsumable);
//    }
//
//    public void init(List<StorageAreaModel> storageAreas, List<GardenAreaModel> gardenAreas) {
//        _areas.clear();
//        _areas.addAll(storageAreas);
//        _areas.addAll(gardenAreas);
//        _gardens = gardenAreas;
//        _storageAreas.clear();
//        _storageAreas.addAll(storageAreas);
//    }
//
//    // TODO: auto onDisplayMultiple in job
//    private void storeConsumable(ConsumableItem consumable) {
////        if (consumable.getJob() != null) {
////            Log.error("AreaModule.storeConsumable: consumable have a job");
////        }
////
////        StorageAreaModel bestStorage = getBestStorage(consumable);
////        if (bestStorage != null && consumable.getStorage() != bestStorage) {
////            Log.info("Consumable have to move in best storage (" + consumable.getInfo().label + " -> " + bestStorage.getName() + ")");
////            jobModule.addJob(StoreJob.create(consumable, bestStorage));
////            return;
////        }
////
////        if (bestStorage != null && consumable.getStorage() == bestStorage) {
//////            Log.debug("Consumable already in best storage (" + consumable.getInfo().label + " -> " + bestStorage.getName() + ")");
////            return;
////        }
////
////        if (bestStorage == null && consumable.getStorage() != null) {
////            Log.debug("Consumable in wrong storage (" + consumable.getInfo().label + ")");
////            ParcelModel parcel = WorldHelper.getNearestFreeParcel(consumable.getParcel(), consumable.getInfo(), consumable.getQuantity());
////            if (parcel != null) {
////                jobModule.addJob(StoreJob.create(consumable, parcel));
////            }
////        }
//    }
////
////    public StorageAreaModel getBestStorage(ConsumableItem consumable) {
////        int bestDistance = Integer.MAX_VALUE;
////        StorageAreaModel bestStorage = null;
////        for (StorageAreaModel storage: _storageAreas) {
////            if ((bestStorage == null || storage.getPriority() >= bestStorage.getPriority()) && storage.accept(consumable.getInfo())) {
////                // Consumable is already in storage area
////                if (consumable.getStorage() == storage) {
////                    bestStorage = storage;
////                    bestDistance = 0;
////                }
////                // Consumable is not in storage area
////                else {
////                    if (storage.hasFreeSpace(consumable.getInfo(), consumable.getQuantity())) {
////                        PathModel path = Application.pathManager.getPath(consumable.getParcel(), storage.getBaseParcel(), false, false);
////                        if (path != null && path.getLength() < bestDistance) {
////                            bestStorage = storage;
////                            bestDistance = path.getLength();
////                        }
////                    }
////                }
////            }
////        }
////        return bestStorage;
////    }
//
//    @Override
//    public void onAddArea(AreaType type, int fromX, int fromY, int toX, int toY, int z) {
//        // Search existing area for current position
//        for (int x = fromX; x <= toX; x++) {
//            for (int y = fromY; y <= toY; y++) {
//                for (AreaModel area: _areas) {
//                    if (area.getType() == type && area.contains(x, y, z)) { addParcelToArea(area, fromX, fromY, toX, toY, z); return; }
//                    if (area.getType() == type && area.contains(x+1, y, z)) { addParcelToArea(area, fromX, fromY, toX, toY, z); return; }
//                    if (area.getType() == type && area.contains(x-1, y, z)) { addParcelToArea(area, fromX, fromY, toX, toY, z); return; }
//                    if (area.getType() == type && area.contains(x, y+1, z)) { addParcelToArea(area, fromX, fromY, toX, toY, z); return; }
//                    if (area.getType() == type && area.contains(x, y-1, z)) { addParcelToArea(area, fromX, fromY, toX, toY, z); return; }
//                }
//            }
//        }
//
//        // Create new area
//        AreaModel area = createArea(type);
//        area.setFloor(z);
//        createArea(area);
//        addParcelToArea(area, fromX, fromY, toX, toY, z);
//    }
//
//    @Override
//    public void onRemoveArea(AreaType type, int fromX, int fromY, int toX, int toY, int z) {
//        // Search existing org.smallbox.faraway.core.module.room.model for current position
//        for (int x = fromX; x <= toX; x++) {
//            for (int y = fromY; y <= toY; y++) {
//                for (AreaModel area: _areas) {
//                    if (area.getType() == type && area.getFloor() == z && area.contains(x, y, z)) {
//                        ParcelModel parcel = worldModule.getParcel(x, y, z);
//                        parcel.setArea(null);
//                        area.removeParcel(parcel);
//                    }
//                }
//            }
//        }
//
//        // Delete empty areas
//        _areas.removeIf(AreaModel::isEmpty);
//        _gardens.removeIf(AreaModel::isEmpty);
//        _storageAreas.removeIf(AreaModel::isEmpty);
//    }
//
////    @Override
////    public void onStorageRulesChanged(StorageAreaModel storage) {
////        // Reset not running store job
////        if (consumableModule.getConsumables() != null) {
////            consumableModule.getConsumables().stream()
////                    .filter(consumable -> consumable.getJob() != null && consumable.getJob() instanceof StoreJob && consumable.getJob().getCharacter() == null)
////                    .forEach(this::storeConsumable);
////        }
////    }
//
//    public static AreaModel createArea(AreaType type) {
//        switch (type) {
//            case STORAGE: return new StorageAreaModel();
//            case GARDEN: return new GardenAreaModel();
//            case HOME: return new HomeAreaModel();
//            default: return null;
//        }
//    }
//
//    private void addParcelToArea(AreaModel area, int fromX, int fromY, int toX, int toY, int z) {
//        for (int x = fromX; x <= toX; x++) {
//            for (int y = fromY; y <= toY; y++) {
//                ParcelModel parcel = worldModule.getParcel(x, y, z);
//
//                // Remove existing plant on parcel
////                if (parcel.hasPlant()) {
////                    JobHelper.addGatherJob(x, y, z, true);
////                }
//
//                // Add parcel to area
//                if (parcelFreeForArea(parcel)) {
//                    area.addParcel(parcel);
//                }
//            }
//        }
//    }
//
//    private boolean parcelFreeForArea(ParcelModel parcel) {
//        if (parcel.hasRock()) {
//            return false;
//        }
//        if (WorldHelper.hasFloor(parcel)) {
//            return false;
//        }
//        if (!parcel.hasGround() || parcel.getGroundInfo().isLinkDown) {
//            return false;
//        }
//        return true;
//    }
//
//    public Collection<AreaModel> getAreas() { return _areas; }
//    public Collection<GardenAreaModel> getGardens() { return _gardens; }
//    public Collection<StorageAreaModel> getStorages() { return _storageAreas; }
//
//    public AreaModel getArea(int x, int y, int z) {
//        for (AreaModel area: _areas) {
//            if (area.contains(x, y, z)) {
//                return area;
//            }
//        }
//        return null;
//    }
//
//    public void createArea(AreaModel area) {
//        _areas.add(area);
//
//        if (area instanceof StorageAreaModel) {
//            _storageAreas.add((StorageAreaModel)area);
//        }
//        if (area instanceof GardenAreaModel) {
//            _gardens.add((GardenAreaModel)area);
//        }
//    }
//
//    public void remove(AreaModel area) {
//        if (area != null) {
//            area.getParcels().forEach(parcel -> parcel.setArea(null));
//            _areas.remove(area);
//
//            if (area instanceof StorageAreaModel) {
//                _storageAreas.remove(area);
//            }
//            if (area instanceof GardenAreaModel) {
//                _gardens.remove(area);
//            }
//        }
//    }
}