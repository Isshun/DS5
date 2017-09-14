package org.smallbox.faraway.modules.plant;

import org.smallbox.faraway.common.modelInfo.ItemInfo;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.area.AreaModel;
import org.smallbox.faraway.modules.area.AreaTypeInfo;

import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

/**
 * Created by Alex on 03/07/2015.
 */
@AreaTypeInfo(label = "Garden")
public class GardenArea extends AreaModel {
    private Collection<ItemInfo>    _potentialItem;
    private ItemInfo                _currentItem;

    public GardenArea() {
        _potentialItem = new LinkedBlockingQueue<>();
        _potentialItem.addAll(Application.data.items.stream().filter(item -> item.isPlant).collect(Collectors.toList()));
    }

    public void cleanField(ParcelModel parcel) {
//        throw new NotImplementedException("");

//        //  Plan to remove plant
//        if (parcel.hasPlant()) {
//            if (parcel.getPlant().getJob() != null) {
//                parcel.getPlant().getJob().cancel();
//            }
//            ModuleHelper.getJobModule().addJob(GatherJob.onCreateJob(parcel.getPlant(), GatherJob.Mode.CUT));
//        }
//
//        //  Plan to remove rock
//        if (parcel.hasRock()) {
//            if (parcel.hasDigJob()) {
//                parcel.getDigJob().cancel();
//            }
//            ModuleHelper.getJobModule().addJob(DigJob.onCreateJob(parcel, parcel.getRockInfo(), null));
//        }
    }

    public void resetField(ParcelModel parcel) {
//        throw new NotImplementedException("");

//        // Put new resource on parcel
//        if (parcel.getPlant() == null && _currentItem != null) {
//            PlantItem plant = (PlantItem) ModuleHelper.getWorldModule().putObject(parcel, _currentItem, 0);
//            plant.setGarden(this);
//            plant.setSeed(false);
//        }
    }

    @Override
    public void addParcel(ParcelModel parcel) {
        super.addParcel(parcel);
        resetField(parcel);
    }

    @Override
    public void setAccept(ItemInfo plantInfo, boolean isAccepted) {
        assert isAccepted;

        // If user set new item
        if (plantInfo != _currentItem) {
            _currentItem = plantInfo;
            _parcels.forEach(this::cleanField);
            _parcels.forEach(this::resetField);
        }

        _currentItem = plantInfo;
    }

    public boolean isAccepted(ItemInfo plantInfo) {
        return _currentItem == plantInfo;
    }

    @Override
    public String getName() {
        return _currentItem != null ? _currentItem.label + " garden" : "Garden";
    }

    public ItemInfo getCurrent() {
        return _currentItem;
    }
    public Collection<ItemInfo> getPotentials() { return _potentialItem; }
}