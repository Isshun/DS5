package org.smallbox.faraway.core.game.module.area.model;

import org.smallbox.faraway.core.data.ItemInfo;
import org.smallbox.faraway.core.game.helper.JobHelper;
import org.smallbox.faraway.core.game.model.GameData;
import org.smallbox.faraway.core.game.module.job.model.GatherJob;
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
        for (ItemInfo itemInfo: GameData.getData().items) {
            if (itemInfo.isPlant) {
                setAccept(itemInfo, false);
                defaultItem = itemInfo;
            }
        }
    }

    private void resetFields() {
        for (ParcelModel parcel: _parcels) {
            resetField(parcel);
        }
    }

    private void resetField(ParcelModel parcel) {
        // Remove previous gather job
        if (parcel.getResource() != null && parcel.getResource().getJob() != null) {
            ModuleHelper.getJobModule().removeJob(parcel.getResource().getJob());
        }

        ResourceModel resource = (ResourceModel)ModuleHelper.getWorldModule().putObject(parcel, _resourceInfo, 0);
        resource.getPlant().setGarden(this);
        resource.getPlant().setSeed(false);

        // Launch new gather job
        JobHelper.addGather(resource, GatherJob.Mode.PLANT_SEED);
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
            resetFields();
        }
    }

    @Override
    public String getName() {
        return _resourceInfo != null ? _resourceInfo.label + " garden" : "Garden";
    }

}
