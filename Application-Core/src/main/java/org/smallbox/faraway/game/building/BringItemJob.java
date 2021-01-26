package org.smallbox.faraway.game.building;

import org.smallbox.faraway.game.consumable.ConsumableItem;
import org.smallbox.faraway.game.job.JobModel;
import org.smallbox.faraway.game.world.Parcel;

public class BringItemJob extends JobModel {
    public ConsumableItem sourceConsumable;

    public BringItemJob(Parcel parcel) {
        super(parcel);
    }

}
