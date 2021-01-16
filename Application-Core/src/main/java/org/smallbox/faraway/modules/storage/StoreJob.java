package org.smallbox.faraway.modules.storage;

import org.smallbox.faraway.core.module.world.model.ConsumableItem;
import org.smallbox.faraway.core.module.world.model.Parcel;
import org.smallbox.faraway.modules.job.JobModel;

public class StoreJob extends JobModel {

    public ConsumableItem sourceConsumable;
    public ConsumableItem targetConsumable;

    public Parcel getStorageParcel() {
        return targetConsumable.getParcel();
    }

}
