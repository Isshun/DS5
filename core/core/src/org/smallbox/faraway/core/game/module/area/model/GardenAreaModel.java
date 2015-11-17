package org.smallbox.faraway.core.game.module.area.model;

import org.smallbox.faraway.core.data.ItemInfo;
import org.smallbox.faraway.core.engine.module.java.ModuleHelper;
import org.smallbox.faraway.core.game.model.Data;
import org.smallbox.faraway.core.game.module.job.model.DigJob;
import org.smallbox.faraway.core.game.module.job.model.GatherJob;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.game.module.world.model.PlantModel;

import java.util.Map;

/**
 * Created by Alex on 03/07/2015.
 */
public class GardenAreaModel extends AreaModel {
    private ItemInfo    _resourceInfo;

    public GardenAreaModel() {
        super(AreaType.GARDEN);

        ItemInfo defaultItem = null;
        for (ItemInfo itemInfo: Data.getData().items) {
            if (itemInfo.isPlant) {
                setAccept(itemInfo, false);
                defaultItem = itemInfo;
            }
        }
    }

    public void cleanField(ParcelModel parcel) {
        //  Plan to remove plant
        if (parcel.hasPlant()) {
            if (parcel.getPlant().getJob() != null) {
                ModuleHelper.getJobModule().removeJob(parcel.getPlant().getJob());
            }
            ModuleHelper.getJobModule().addJob(GatherJob.create(parcel.getPlant(), GatherJob.Mode.CUT));
        }

        //  Plan to remove rock
        if (parcel.hasRock()) {
            if (parcel.hasDigJob()) {
                ModuleHelper.getJobModule().removeJob(parcel.getDigJob());
            }
            ModuleHelper.getJobModule().addJob(DigJob.create(parcel, parcel.getRockInfo(), null));
        }
    }

    public void resetField(ParcelModel parcel) {
        if (parcel.getPlant() == null) {
            // Put new resource on parcel
            PlantModel resource = (PlantModel) ModuleHelper.getWorldModule().putObject(parcel, _resourceInfo, 0);
            resource.setGarden(this);
            resource.setSeed(false);
        }
    }

    @Override
    public void addParcel(ParcelModel parcel) {
        super.addParcel(parcel);
        resetField(parcel);
    }

    @Override
    public void setAccept(ItemInfo itemInfo, boolean isAccepted) {
        // Reset current state
        _resourceInfo = null;
        for (Map.Entry<ItemInfo, Boolean> entry: _items.entrySet()) {
            _items.put(entry.getKey(), false);
        }

        // If user set new item
        if (itemInfo != null) {
            _items.put(itemInfo, true);
            _resourceInfo = itemInfo;
            _parcels.forEach(this::cleanField);
            _parcels.forEach(this::resetField);
        }
    }

    @Override
    public String getName() {
        return _resourceInfo != null ? _resourceInfo.label + " garden" : "Garden";
    }

    public ItemInfo getAccepted() {
        return _resourceInfo;
    }
}
