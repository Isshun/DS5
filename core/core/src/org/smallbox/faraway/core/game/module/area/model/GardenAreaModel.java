package org.smallbox.faraway.core.game.module.area.model;

import org.smallbox.faraway.core.data.ItemInfo;
import org.smallbox.faraway.core.game.helper.JobHelper;
import org.smallbox.faraway.core.game.model.Data;
import org.smallbox.faraway.core.game.module.job.model.GatherJob;
import org.smallbox.faraway.core.game.module.job.model.MineJob;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.game.module.world.model.resource.ResourceModel;
import org.smallbox.faraway.core.module.java.ModuleHelper;

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
        ResourceModel resource = parcel.getResource();
        if (resource != null) {
            // Remove previous job
            if (resource.getJob() != null) {
                ModuleHelper.getJobModule().removeJob(resource.getJob());
            }

            //  Plan to cut / remove resource
            if (resource.canBeMined()) {
                ModuleHelper.getJobModule().addJob(MineJob.create(resource));
            } else if (resource.canBeHarvested()) {
                ModuleHelper.getJobModule().addJob(GatherJob.create(resource, GatherJob.Mode.CUT));
            }
        }
    }

    public void resetField(ParcelModel parcel) {
        if (parcel.getResource() == null) {
            // Put new resource on parcel
            ResourceModel resource = (ResourceModel) ModuleHelper.getWorldModule().putObject(parcel, _resourceInfo, 0);
            resource.getPlant().setGarden(this);
            resource.getPlant().setSeed(false);

            // Launch new gather job
            JobHelper.addGather(resource, GatherJob.Mode.PLANT_SEED);
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
