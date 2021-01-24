package org.smallbox.faraway.game.consumable;

import org.smallbox.faraway.game.job.JobModel;

public class ConsumeJob extends JobModel {

    public ConsumableItem _consumable;
    public ConsumableModule.ConsumableJobLock _lock;
    public double _duration;

    public interface OnConsumeCallback {
        /**
         * Methode appelée à chaque tick tant que l'action n'est pas terminée
         * @param consumable le consomable
         * @param durationLeft la durée restante
         */
        void onConsume(ConsumableItem consumable, double durationLeft);
    }

}
