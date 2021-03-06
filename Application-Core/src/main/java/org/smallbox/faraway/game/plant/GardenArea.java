package org.smallbox.faraway.game.plant;

import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotation.callback.applicationEvent.OnInit;
import org.smallbox.faraway.core.game.DataManager;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.game.area.AreaModel;
import org.smallbox.faraway.game.area.AreaTypeInfo;
import org.smallbox.faraway.game.world.Parcel;

import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

@GameObject
@AreaTypeInfo(label = "Garden", color = 0x47a63aff)
public class GardenArea extends AreaModel {
    private Collection<ItemInfo>    _potentialItem;
    private ItemInfo                _currentItem;
    @Inject private GardenModule gardenModule;
    @Inject private DataManager dataManager;

    @OnInit
    public void init() {
        _potentialItem = new LinkedBlockingQueue<>();
        _potentialItem.addAll(dataManager.items.stream().filter(item -> item.isPlant).collect(Collectors.toList()));
    }

    public void cleanField(Parcel parcel) {
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

    public void resetField(Parcel parcel) {
//        throw new NotImplementedException("");

//        // Put new resource on parcel
//        if (parcel.getPlant() == null && _currentItem != null) {
//            PlantItem plant = (PlantItem) ModuleHelper.getWorldModule().putObject(parcel, _currentItem, 0);
//            plant.setGarden(this);
//            plant.setSeed(false);
//        }
    }

    @Override
    public void addParcel(Parcel parcel) {
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

    @Override
    public void onParcelSelected(Parcel parcel) {
        gardenModule.addParcel(parcel);
    }
}