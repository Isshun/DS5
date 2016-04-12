package org.smallbox.faraway.core.game.module.area.model;

import org.smallbox.faraway.core.engine.module.java.ModuleHelper;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.game.module.job.model.DigJob;
import org.smallbox.faraway.core.game.module.job.model.GatherJob;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.game.module.world.model.PlantModel;

import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

/**
 * Created by Alex on 03/07/2015.
 */
public class GardenAreaModel extends AreaModel {
    private Collection<ItemInfo>    _potentialItem;
    private ItemInfo                _currentItem;

    public GardenAreaModel() {
        super(AreaType.GARDEN);

        _potentialItem = new LinkedBlockingQueue<>();
        _potentialItem.addAll(Data.getData().items.stream().filter(item -> item.isPlant).collect(Collectors.toList()));
    }

    public void cleanField(ParcelModel parcel) {
        //  Plan to remove plant
        if (parcel.hasPlant()) {
            if (parcel.getPlant().getJob() != null) {
                parcel.getPlant().getJob().cancel();
            }
            ModuleHelper.getJobModule().addJob(GatherJob.create(parcel.getPlant(), GatherJob.Mode.CUT));
        }

        //  Plan to remove rock
        if (parcel.hasRock()) {
            if (parcel.hasDigJob()) {
                parcel.getDigJob().cancel();
            }
            ModuleHelper.getJobModule().addJob(DigJob.create(parcel, parcel.getRockInfo(), null));
        }
    }

    public void resetField(ParcelModel parcel) {
        // Put new resource on parcel
        if (parcel.getPlant() == null && _currentItem != null) {
            PlantModel plant = (PlantModel) ModuleHelper.getWorldModule().putObject(parcel, _currentItem, 0);
            plant.setGarden(this);
            plant.setSeed(false);
        }
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

    public boolean accept(ItemInfo plantInfo) {
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