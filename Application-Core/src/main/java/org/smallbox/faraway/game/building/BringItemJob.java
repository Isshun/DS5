package org.smallbox.faraway.game.building;

import org.smallbox.faraway.game.consumable.Consumable;
import org.smallbox.faraway.game.job.JobModel;
import org.smallbox.faraway.game.world.Parcel;

public class BringItemJob extends JobModel {
    public Consumable sourceConsumable;
    public Consumable inventoryConsumable;

    public BringItemJob(Parcel parcel) {
        super(parcel);
    }

}
