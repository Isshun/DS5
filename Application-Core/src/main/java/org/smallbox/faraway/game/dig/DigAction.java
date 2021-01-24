package org.smallbox.faraway.game.dig;

import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.game.world.Parcel;
import org.smallbox.faraway.game.area.AreaModel;
import org.smallbox.faraway.game.area.AreaTypeInfo;
import org.smallbox.faraway.game.job.JobModule;

@GameObject
@AreaTypeInfo(label = "Dig", color = 0x80391eff)
public class DigAction extends AreaModel {
    @Inject private JobModule jobModule;
    @Inject private DigJobFactory digJobFactory;

    @Override
    public boolean isAccepted(ItemInfo itemInfo) {
        return false;
    }

    @Override
    public void setAccept(ItemInfo itemInfo, boolean isAccepted) {
    }

    @Override
    public void onParcelSelected(Parcel parcel) {
        if (parcel.getRockInfo() != null) {
            jobModule.add(digJobFactory.createJob(parcel));
        }
    }

}
