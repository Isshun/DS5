package org.smallbox.faraway.core.game.module.area;

import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.helper.JobHelper;
import org.smallbox.faraway.core.game.model.Data;
import org.smallbox.faraway.core.game.module.area.model.*;
import org.smallbox.faraway.core.game.module.job.model.StoreJob;
import org.smallbox.faraway.core.game.module.path.PathManager;
import org.smallbox.faraway.core.game.module.world.WorldModule;
import org.smallbox.faraway.core.game.module.world.model.ConsumableModel;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.engine.module.java.ModuleHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Alex on 13/06/2015.
 */
public class AreaModule extends GameModule {
    private List<AreaModel> _areas = new ArrayList<>();
    private List<StorageAreaModel> _storageAreas = new ArrayList<>();

    public AreaModule() {
        _updateInterval = 10;
    }

    @Override
    protected void onLoaded(Game game) {
    }

    @Override
    protected void onUpdate(int tick) {
        Collections.sort(_storageAreas, (o1, o2) -> o2.getPriority() - o1.getPriority());

        // Create store jobs
//        _jobs.stream().filter(job -> job instanceof JobHaul).forEach(job -> ((JobHaul)job).foundConsumablesAround());
        ModuleHelper.getWorldModule().getConsumables().stream().filter(consumable -> consumable.getStoreJob() == null).forEach(this::storeConsumable);
    }

    private void storeConsumable(ConsumableModel consumable) {
        if (consumable.getStoreJob() != null) {
            ModuleHelper.getJobModule().removeJob(consumable.getStoreJob());
        }

        StorageAreaModel bestStorage = getBestStorage(consumable);
        if (bestStorage != null && consumable.getStorage() != bestStorage) {
            System.out.println("Consumable have to move in best storage (" + consumable.getInfo().label + " -> " + bestStorage.getName() + ")");
            ModuleHelper.getJobModule().addJob(StoreJob.create(consumable, bestStorage));
        } else if (bestStorage != null) {
//            System.out.println("Consumable already in best storage (" + consumable.getInfo().label + " -> " + bestStorage.getName() + ")");
        } else {
//            System.out.println("No best storage for " + consumable.getInfo().label);
        }
    }

    public StorageAreaModel getBestStorage(ConsumableModel consumable) {
        StorageAreaModel bestStorage = null;
        for (StorageAreaModel storage: _storageAreas) {
            if (storage.accept(consumable.getInfo())
                    && storage.hasFreeSpace(consumable.getInfo(), consumable.getQuantity())
                    && PathManager.getInstance().hasPath(consumable.getParcel(), storage.getBaseParcel())) {
                bestStorage = storage;
                break;
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
        _areas.removeAll(_areas.stream().filter(area -> area.getParcels().isEmpty()).collect(Collectors.toList()));
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
            default: return new AreaModel(type);
        }
    }

    private void addParcelToArea(AreaModel area, int fromX, int fromY, int toX, int toY, int z) {
        WorldModule worldModule = ModuleHelper.getWorldModule();

        for (int x = fromX; x <= toX; x++) {
            for (int y = fromY; y <= toY; y++) {
                ParcelModel parcel = worldModule.getParcel(x, y, z);

                // Remove existing resource on parcel
                if (parcel.hasPlant()) {
                    if (parcel.hasRock()) {
                        JobHelper.addMineJob(x, y, z, false);
                    } else if (parcel.hasPlant()) {
                        JobHelper.addGatherJob(x, y, z, true);
                    }
                }

                // Add parcel to area
                area.addParcel(parcel);
            }
        }
    }

    public List<AreaModel> getAreas() {
        return _areas;
    }

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
    }

    public void remove(AreaModel area) {
        if (area != null) {
            area.getParcels().forEach(parcel -> parcel.setArea(null));
            _areas.remove(area);

            if (area instanceof StorageAreaModel) {
                _storageAreas.remove(area);
            }
        }
    }

    @Override
    protected boolean loadOnStart() {
        return Data.config.manager.area;
    }

}
