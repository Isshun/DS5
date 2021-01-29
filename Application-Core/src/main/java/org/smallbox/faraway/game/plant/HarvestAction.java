package org.smallbox.faraway.game.plant;

import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.game.area.AreaModel;
import org.smallbox.faraway.game.area.AreaTypeInfo;
import org.smallbox.faraway.game.job.JobModule;
import org.smallbox.faraway.game.world.Parcel;

@GameObject
@AreaTypeInfo(label = "Harvest", color = 0x391e80ff)
public class HarvestAction extends AreaModel {
    @Inject private HarvestJobFactory harvestJobFactory;
    @Inject private PlantModule plantModule;
    @Inject private JobModule jobModule;

    @Override
    public boolean isAccepted(ItemInfo itemInfo) {
        return false;
    }

    @Override
    public void setAccept(ItemInfo itemInfo, boolean isAccepted) {
    }

    @Override
    public void onParcelSelected(Parcel parcel) {
        plantModule.getAll().stream().filter(plant -> plant.getParcel() == parcel).forEach(plant -> jobModule.add(harvestJobFactory.createJob(plant)));
    }

}
