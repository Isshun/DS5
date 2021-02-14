package org.smallbox.faraway.game.area;

import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.game.job.JobModule;
import org.smallbox.faraway.game.world.Parcel;

@GameObject
@AreaTypeInfo(label = "Remove job", color = 0xa8a8a8ff)
public class RemoveJob extends AreaModel {
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
        jobModule.getAll().stream().filter(job -> job.getTargetParcel() == parcel).forEach(job -> jobModule.remove(job));
    }

}
