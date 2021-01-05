package org.smallbox.faraway.modules.consumable;

import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.module.world.model.ConsumableItem;
import org.smallbox.faraway.modules.job.JobTaskReturn;

@GameObject
public class ConsumeJobFactory {

    @Inject
    private ConsumableModule consumableModule;

    @Inject
    private Game game;

    public ConsumeJob create(ConsumableItem consumable, double totalDuration, ConsumeJob.OnConsumeCallback callback) {
        ConsumeJob job = new ConsumeJob();

        job._consumable = consumable;
        job._lock = consumableModule.lock(job, consumable, 1);
        job._targetParcel = consumable.getParcel();

        job.setMainLabel("Consume " + consumable.getInfo().label);

        job.addMoveTask("Move", consumable::getParcel);
        job.addTask("Consume", (character, hourInterval) -> {
            if (job._lock.available) {
                // TODO
                job._duration += 1 / game.getTickPerHour();
                double durationLeft = totalDuration - job._duration;
                callback.onConsume(consumable, durationLeft);
                job.setProgress(job._duration, totalDuration);

                if (durationLeft > 0) {
                    return JobTaskReturn.TASK_CONTINUE;
                }

                // Retire le lock si l'action est terminée
                consumableModule.createConsumableFromLock(job._lock);
                return JobTaskReturn.TASK_COMPLETED;
            }
            return JobTaskReturn.TASK_ERROR;
        });

        job.addCloseTask(() -> {
            if (job._lock.available) {
                consumableModule.cancelLock(job._lock);
            }

            if (job._consumable != null) {
                job._consumable.removeJob(job);
            }
        });

        return job;
    }

}
