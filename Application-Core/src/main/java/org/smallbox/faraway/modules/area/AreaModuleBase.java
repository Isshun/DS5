package org.smallbox.faraway.modules.area;

import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.storage.StorageArea;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public abstract class AreaModuleBase<T_AREA extends AreaModel> extends GameModule implements AreaModuleListener {
    protected Queue<T_AREA> areas = new ConcurrentLinkedQueue<>();

    public void addParcel(ParcelModel parcel) {
        List<T_AREA> matchingAreas = this.areas.stream().filter(areaModel -> areaModel.haveParcelNextTo(parcel)).collect(Collectors.toList());

        // Only one storage area exists, add new parcel to them
        if (matchingAreas.size() == 1) {
            matchingAreas.get(0).addParcel(parcel);
        }

        // Two or more storage area exists, merge all of them and add new parcel to a new storage
        if (matchingAreas.size() > 1) {
            T_AREA area = onNewArea();
            area.addParcel(parcel);
            for (T_AREA matchingArea: matchingAreas) {
                for (ParcelModel matchingStorageAreaParcel: matchingArea.getParcels()) {
                    area.addParcel(matchingStorageAreaParcel);
                }
            }
            this.areas.add(area);
            this.areas.removeAll(matchingAreas);
        }

        // Create new storage area if not exists
        if (matchingAreas.isEmpty()) {
            T_AREA area = onNewArea();
            area.addParcel(parcel);
            this.areas.add(area);
        }

    }

    public Queue<T_AREA> getAreas() {
        return areas;
    }

    public abstract T_AREA onNewArea();

    @Override
    public void onRemoveParcel(ParcelModel parcel) {
        areas.forEach(storageArea -> storageArea.removeParcel(parcel));
        areas.removeIf(
                AreaModel::isEmpty);
    }
}
