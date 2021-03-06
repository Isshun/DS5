package org.smallbox.faraway.game.storage;

import org.smallbox.faraway.game.consumable.Consumable;
import org.smallbox.faraway.game.job.JobModel;
import org.smallbox.faraway.game.world.Parcel;

public class StoreJob extends JobModel {
    public Consumable sourceConsumable;
    public Consumable inventoryConsumable;
    public StorageArea storageArea;
    public Parcel storageParcel;

    public StoreJob(Parcel parcel) {
        super(parcel);
    }

}
