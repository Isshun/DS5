package org.smallbox.faraway.modules.dig;

import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.model.MovableModel;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.world.model.Parcel;
import org.smallbox.faraway.modules.area.AreaModel;
import org.smallbox.faraway.modules.area.AreaTypeInfo;
import org.smallbox.faraway.modules.job.JobModule;
import org.smallbox.faraway.modules.world.WorldModule;

import java.util.Optional;

@GameObject
@AreaTypeInfo(label = "Dig under", color = 0x80391eff)
public class DigUnderAction extends AreaModel {
    @Inject private JobModule jobModule;
    @Inject private DigJobFactory digJobFactory;
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
        Optional.ofNullable(worldModule.getParcel(parcel, MovableModel.Direction.UNDER)).filter(Parcel::hasRock).ifPresent(
                parcelUnder -> jobModule.add(digJobFactory.createJob(parcelUnder)));
    }

}
