package org.smallbox.faraway.game.storage;

import org.smallbox.faraway.game.consumable.ConsumableItem;
import org.smallbox.faraway.game.world.Parcel;
import org.smallbox.faraway.game.job.JobModel;

public class StoreJob extends JobModel {

    public ConsumableItem sourceConsumable;
    public ConsumableItem targetConsumable;

    public StoreJob(Parcel parcel) {
        super(parcel);
    }

    public Parcel getStorageParcel() {
        return targetConsumable.getParcel();
    }

}
