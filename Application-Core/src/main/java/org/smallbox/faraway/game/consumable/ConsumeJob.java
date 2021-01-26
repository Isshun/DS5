package org.smallbox.faraway.game.consumable;

import org.smallbox.faraway.game.job.JobModel;
import org.smallbox.faraway.game.world.Parcel;

public class ConsumeJob extends JobModel {
    public ConsumableModule.ConsumableJobLock _lock;

    public ConsumeJob(Parcel parcel) {
        super(parcel);
    }

    public interface OnConsumeCallback {
        /**
         * Methode appelée à chaque tick tant que l'action n'est pas terminée
         * @param consumable le consomable
         * @param durationLeft la durée restante
         */
        void onConsume(ConsumableItem consumable, double durationLeft);
    }

}
