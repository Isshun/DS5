package org.smallbox.faraway.modules.dig;

import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.area.AreaModel;
import org.smallbox.faraway.modules.area.AreaTypeInfo;
import org.smallbox.faraway.modules.consumable.ConsumableModule;
import org.smallbox.faraway.modules.job.JobModule;
import org.smallbox.faraway.modules.world.WorldModule;

@GameObject
@AreaTypeInfo(label = "Dig", color = 0x80391eff)
public class DigArea extends AreaModel {

    @Inject
    private JobModule jobModule;

    @Inject
    private ConsumableModule consumableModule;

    @Inject
    private WorldModule worldModule;

    @Inject
    private DigModule digModule;

    @Inject
    private DigJobFactory digJobFactory;

    @Override
    public boolean isAccepted(ItemInfo itemInfo) {
        return false;
    }

    @Override
    public void setAccept(ItemInfo itemInfo, boolean isAccepted) {

    }

    @Override
    public void onParcelSelected(ParcelModel parcel) {
        if (parcel.getRockInfo() != null) {
            jobModule.addJob(digJobFactory.createJob(parcel));
        }
    }

}
