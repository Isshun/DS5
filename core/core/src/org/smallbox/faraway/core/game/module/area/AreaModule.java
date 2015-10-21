package org.smallbox.faraway.core.game.module.area;

import org.smallbox.faraway.core.data.serializer.SerializerInterface;
import org.smallbox.faraway.core.game.model.GameData;
import org.smallbox.faraway.core.game.module.area.model.*;
import org.smallbox.faraway.core.game.module.world.model.ConsumableModel;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.game.module.job.model.StoreJob;
import org.smallbox.faraway.core.module.GameModule;
import org.smallbox.faraway.core.game.module.world.WorldModule;
import org.smallbox.faraway.core.module.java.ModuleHelper;
import org.smallbox.faraway.core.game.module.path.PathManager;
import org.smallbox.faraway.core.util.Utils;

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
    protected void onLoaded() {

    }

    @Override
    protected void onUpdate(int tick) {
        Collections.sort(_storageAreas, (o1, o2) -> o2.getPriority() - o1.getPriority());

        // Create store jobs
//        _jobs.stream().filter(job -> job instanceof JobHaul).forEach(job -> ((JobHaul)job).foundConsumablesAround());
        ModuleHelper.getWorldModule().getConsumables().stream().filter(consumable -> consumable.getStoreJob() == null).forEach(this::storeConsumable);
    }

    private void storeConsumable(ConsumableModel consumable) {
        StorageAreaModel bestStorage = getBestStorage(consumable);
        if (bestStorage != null && consumable.getStorage() != bestStorage) {
            System.out.println("Consumable have to move in best storage (" + consumable.getInfo().label + " -> " + bestStorage.getName() + ")");
            ModuleHelper.getJobModule().addJob(StoreJob.create(consumable, bestStorage));
        } else if (bestStorage != null) {
            System.out.println("Consumable already in best storage (" + consumable.getInfo().label + " -> " + bestStorage.getName() + ")");
        } else {
            System.out.println("No best storage for " + consumable.getInfo().label);
        }
    }

    public StorageAreaModel getBestStorage(ConsumableModel consumable) {
        StorageAreaModel bestStorage = null;
        for (StorageAreaModel storage: _storageAreas) {
            if (storage.accept(consumable.getInfo()) && storage.hasFreeSpace(consumable.getInfo(), consumable.getQuantity())) {
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
    public void onAddArea(AreaType type, int fromX, int fromY, int toX, int toY) {
        // Search existing model for current position
        for (int x = fromX; x <= toX; x++) {
            for (int y = fromY; y <= toY; y++) {
                for (AreaModel area: _areas) {
                    if (area.getType() == type && area.contains(x, y)) {
                        addParcelToArea(area, fromX, fromY, toX, toY);
                        return;
                    }
                }
            }
        }

        // Create new model
        AreaModel area = createArea(type);
        addArea(area);
        addParcelToArea(area, fromX, fromY, toX, toY);
    }

    @Override
    public void onRemoveArea(AreaType type, int fromX, int fromY, int toX, int toY) {
        // Search existing model for current position
        for (int x = fromX; x <= toX; x++) {
            for (int y = fromY; y <= toY; y++) {
                for (AreaModel area: _areas) {
                    if (area.getType() == type && area.contains(x, y)) {
                        ParcelModel parcel = ModuleHelper.getWorldModule().getParcel(x, y);
                        parcel.setArea(null);
                        area.removeParcel(parcel);
                    }
                }
            }
        }

        // Delete empty areas
        _areas.removeAll(_areas.stream().filter(area -> area.getParcels().isEmpty()).collect(Collectors.toList()));
    }

    public static AreaModel createArea(AreaType type) {
        switch (type) {
            case STORAGE: return new StorageAreaModel();
            case GARDEN: return new GardenAreaModel();
            case HOME: return new HomeAreaModel();
            default: return new AreaModel(type);
        }
    }

    private void addParcelToArea(AreaModel area, int fromX, int fromY, int toX, int toY) {
        WorldModule worldModule = ModuleHelper.getWorldModule();

        for (int x = fromX; x <= toX; x++) {
            for (int y = fromY; y <= toY; y++) {
                area.addParcel(worldModule.getParcel(x, y));
            }
        }
    }

    public List<AreaModel> getAreas() {
        return _areas;
    }

    public AreaModel getArea(int x, int y) {
        for (AreaModel area: _areas) {
            if (area.contains(x, y)) {
                return area;
            }
        }
        return null;
    }

    public StorageAreaModel getNearestFreeStorage(ConsumableModel consumable, ParcelModel fromParcel) {
        int bestDistance = Integer.MAX_VALUE;
        AreaModel bestArea = null;
        for (AreaModel area: _areas) {
            if (area.isStorage() && area.accept(consumable.getInfo()) && PathManager.getInstance().getPath(area, fromParcel) != null) {
                ParcelModel parcel = ((StorageAreaModel)area).getNearestFreeParcel(consumable, fromParcel);
                if (parcel != null && Utils.getDistance(parcel, fromParcel) < bestDistance) {
                    bestArea = area;
                    bestDistance = Utils.getDistance(parcel, fromParcel);
                }
            }
        }
        return (StorageAreaModel)bestArea;
    }

    public ParcelModel getNearestFreeStorageParcel(ConsumableModel consumable, ParcelModel fromParcel) {
        int bestDistance = Integer.MAX_VALUE;
        ParcelModel bestParcel = null;
        for (AreaModel area: _areas) {
            if (area.isStorage() && area.accept(consumable.getInfo()) && PathManager.getInstance().getPath(area, fromParcel) != null) {
                ParcelModel parcel = ((StorageAreaModel)area).getNearestFreeParcel(consumable, fromParcel);
                if (parcel != null && Utils.getDistance(parcel, fromParcel) < bestDistance) {
                    bestParcel = parcel;
                    bestDistance = Utils.getDistance(parcel, fromParcel);
                }
            }
        }
        return bestParcel;
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
    public SerializerInterface getSerializer() {
        return new AreaModuleSerializer();
    }

    @Override
    protected boolean loadOnStart() {
        return GameData.config.manager.area;
    }

}
