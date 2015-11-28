package org.smallbox.faraway.core.game.module.area;

import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.engine.module.java.ModuleHelper;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.helper.JobHelper;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.module.area.model.*;
import org.smallbox.faraway.core.game.module.character.model.PathModel;
import org.smallbox.faraway.core.game.module.job.model.StoreJob;
import org.smallbox.faraway.core.game.module.path.PathManager;
import org.smallbox.faraway.core.game.module.world.WorldModule;
import org.smallbox.faraway.core.game.module.world.model.ConsumableModel;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.util.Log;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Alex on 13/06/2015.
 */
public class AreaModule extends GameModule {
    private Collection<AreaModel> _areas = new LinkedBlockingQueue<>();
    private Collection<GardenAreaModel> _gardens = new LinkedBlockingQueue<>();
    private Collection<StorageAreaModel> _storageAreas = new LinkedBlockingQueue<>();

    public AreaModule() {
        _updateInterval = 10;
    }

    @Override
    protected void onGameStart(Game game) {
    }

    @Override
    protected void onUpdate(int tick) {
        // Create store jobs
//        _jobs.stream().filter(job -> job instanceof JobHaul).forEach(job -> ((JobHaul)job).foundConsumablesAround());
        ModuleHelper.getWorldModule().getConsumables().stream()
                .filter(consumable -> consumable.getStoreJob() == null)
                .forEach(this::storeConsumable);
    }

    public void init(List<StorageAreaModel> storageAreas, List<GardenAreaModel> gardenAreas) {
        _areas.clear();
        _areas.addAll(storageAreas);
        _areas.addAll(gardenAreas);
        _gardens = gardenAreas;
        _storageAreas.clear();
        _storageAreas.addAll(storageAreas);
    }

    private void storeConsumable(ConsumableModel consumable) {
        if (consumable.getStoreJob() != null) {
            consumable.getStoreJob().cancel();
        }

        StorageAreaModel bestStorage = getBestStorage(consumable);
        if (bestStorage != null && consumable.getStorage() != bestStorage) {
            Log.info("Consumable have to move in best storage (" + consumable.getInfo().label + " -> " + bestStorage.getName() + ")");
            ModuleHelper.getJobModule().addJob(StoreJob.create(consumable, bestStorage));
            return;
        }

        if (bestStorage != null && consumable.getStorage() == bestStorage) {
//            Log.debug("Consumable already in best storage (" + consumable.getInfo().label + " -> " + bestStorage.getName() + ")");
            return;
        }

        if (bestStorage == null && consumable.getStorage() != null) {
            Log.debug("Consumable in wrong storage (" + consumable.getInfo().label + ")");
            ParcelModel parcel = WorldHelper.getNearestFreeParcel(consumable.getParcel(), consumable.getInfo(), consumable.getQuantity());
            if (parcel != null) {
                ModuleHelper.getJobModule().addJob(StoreJob.create(consumable, parcel));
            }
        }
    }

    public StorageAreaModel getBestStorage(ConsumableModel consumable) {
        int bestDistance = Integer.MAX_VALUE;
        StorageAreaModel bestStorage = null;
        for (StorageAreaModel storage: _storageAreas) {
            if ((bestStorage == null || storage.getPriority() >= bestStorage.getPriority()) && storage.accept(consumable.getInfo())) {
                // Consumable is already in storage area
                if (consumable.getStorage() == storage) {
                    bestStorage = storage;
                    bestDistance = 0;
                }
                // Consumable is not in storage area
                else {
                    if (storage.hasFreeSpace(consumable.getInfo(), consumable.getQuantity())) {
                        PathModel path = PathManager.getInstance().getPath(consumable.getParcel(), storage.getBaseParcel(), false, false);
                        if (path != null && path.getLength() < bestDistance) {
                            bestStorage = storage;
                            bestDistance = path.getLength();
                        }
                    }
                }
            }
        }
        return bestStorage;
    }

    @Override
    public void onAddConsumable(ConsumableModel consumable) {
        if (consumable.getStoreJob() == null) {
            storeConsumable(consumable);
        }
    }

    @Override
    public void onAddArea(AreaType type, int fromX, int fromY, int toX, int toY, int z) {
        // Search existing area for current position
        for (int x = fromX; x <= toX; x++) {
            for (int y = fromY; y <= toY; y++) {
                for (AreaModel area: _areas) {
                    if (area.getType() == type && area.contains(x, y, z)) { addParcelToArea(area, fromX, fromY, toX, toY, z); return; }
                    if (area.getType() == type && area.contains(x+1, y, z)) { addParcelToArea(area, fromX, fromY, toX, toY, z); return; }
                    if (area.getType() == type && area.contains(x-1, y, z)) { addParcelToArea(area, fromX, fromY, toX, toY, z); return; }
                    if (area.getType() == type && area.contains(x, y+1, z)) { addParcelToArea(area, fromX, fromY, toX, toY, z); return; }
                    if (area.getType() == type && area.contains(x, y-1, z)) { addParcelToArea(area, fromX, fromY, toX, toY, z); return; }
                }
            }
        }

        // Create new area
        AreaModel area = createArea(type);
        area.setFloor(z);
        addArea(area);
        addParcelToArea(area, fromX, fromY, toX, toY, z);

        // Reset not running store job
        ModuleHelper.getWorldModule().getConsumables().stream()
                .filter(consumable -> consumable.getStoreJob() != null && consumable.getStoreJob().getCharacter() == null)
                .forEach(this::storeConsumable);
    }

    @Override
    public void onRemoveArea(AreaType type, int fromX, int fromY, int toX, int toY, int z) {
        // Search existing model for current position
        for (int x = fromX; x <= toX; x++) {
            for (int y = fromY; y <= toY; y++) {
                for (AreaModel area: _areas) {
                    if (area.getType() == type && area.getFloor() == z && area.contains(x, y, z)) {
                        ParcelModel parcel = ModuleHelper.getWorldModule().getParcel(x, y, z);
                        parcel.setArea(null);
                        area.removeParcel(parcel);
                    }
                }
            }
        }

        // Delete empty areas
        _areas.removeIf(AreaModel::isEmpty);
        _gardens.removeIf(AreaModel::isEmpty);
        _storageAreas.removeIf(AreaModel::isEmpty);
    }

    @Override
    public void onStorageRulesChanged(StorageAreaModel storage) {
        // Reset not running store job
        ModuleHelper.getWorldModule().getConsumables().stream()
                .filter(consumable -> consumable.getStoreJob() != null && consumable.getStoreJob().getCharacter() == null)
                .forEach(this::storeConsumable);
    }

    public static AreaModel createArea(AreaType type) {
        switch (type) {
            case STORAGE: return new StorageAreaModel();
            case GARDEN: return new GardenAreaModel();
            case HOME: return new HomeAreaModel();
            default: return null;
        }
    }

    private void addParcelToArea(AreaModel area, int fromX, int fromY, int toX, int toY, int z) {
        WorldModule worldModule = ModuleHelper.getWorldModule();

        for (int x = fromX; x <= toX; x++) {
            for (int y = fromY; y <= toY; y++) {
                ParcelModel parcel = worldModule.getParcel(x, y, z);

                // Remove existing plant on parcel
                if (parcel.hasPlant()) {
                    JobHelper.addGatherJob(x, y, z, true);
                }

                // Add parcel to area
                if (parcelFreeForArea(parcel)) {
                    area.addParcel(parcel);
                }
            }
        }
    }

    private boolean parcelFreeForArea(ParcelModel parcel) {
        if (parcel.hasRock()) {
            return false;
        }
        if (parcel.hasStructure() && !parcel.getStructure().isFloor()) {
            return false;
        }
        if (!parcel.hasGround() || parcel.getGroundInfo().isLinkDown) {
            return false;
        }
        return true;
    }

    public Collection<AreaModel> getAreas() { return _areas; }
    public Collection<GardenAreaModel> getGardens() { return _gardens; }
    public Collection<StorageAreaModel> getStorages() { return _storageAreas; }

    public AreaModel getArea(int x, int y, int z) {
        for (AreaModel area: _areas) {
            if (area.contains(x, y, z)) {
                return area;
            }
        }
        return null;
    }

    public void addArea(AreaModel area) {
        _areas.add(area);

        if (area instanceof StorageAreaModel) {
            _storageAreas.add((StorageAreaModel)area);
        }
        if (area instanceof GardenAreaModel) {
            _gardens.add((GardenAreaModel)area);
        }
    }

    public void remove(AreaModel area) {
        if (area != null) {
            area.getParcels().forEach(parcel -> parcel.setArea(null));
            _areas.remove(area);

            if (area instanceof StorageAreaModel) {
                _storageAreas.remove(area);
            }
            if (area instanceof GardenAreaModel) {
                _gardens.remove(area);
            }
        }
    }
}