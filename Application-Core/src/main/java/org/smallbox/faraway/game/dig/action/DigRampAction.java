package org.smallbox.faraway.game.dig.action;

import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.game.area.AreaModel;
import org.smallbox.faraway.game.area.AreaTypeInfo;
import org.smallbox.faraway.game.dig.factory.DigRampJobFactory;
import org.smallbox.faraway.game.job.JobModule;
import org.smallbox.faraway.game.world.Parcel;
import org.smallbox.faraway.game.world.WorldModule;

@GameObject
@AreaTypeInfo(label = "Dig ramp", color = 0x80391eff)
public class DigRampAction extends AreaModel {
    @Inject private JobModule jobModule;
    @Inject private DigRampJobFactory digRampJobFactory;
    @Inject private WorldModule worldModule;

    @Override
    public boolean isAccepted(ItemInfo itemInfo) {
        return false;
    }

    @Override
    public void setAccept(ItemInfo itemInfo, boolean isAccepted) {
    }

    @Override
    public void onParcelSelected(Parcel parcel) {
        if (parcel.hasRock()) {
            jobModule.add(digRampJobFactory.createJob(parcel));
        }
    }

}
