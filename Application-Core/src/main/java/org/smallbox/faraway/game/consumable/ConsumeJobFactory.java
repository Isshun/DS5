package org.smallbox.faraway.game.consumable;

import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.game.job.task.ActionTask;
import org.smallbox.faraway.game.world.SurroundedPattern;
import org.smallbox.faraway.game.world.WorldHelper;

import static org.smallbox.faraway.game.job.JobTaskReturn.TASK_COMPLETED;
import static org.smallbox.faraway.game.job.JobTaskReturn.TASK_CONTINUE;

@GameObject
public class ConsumeJobFactory {
    @Inject private ConsumableModule consumableModule;

    public ConsumeJob create(Consumable consumable, ConsumeJob.OnConsumeCallback callback) {
        ConsumeJob job = new ConsumeJob(consumable.getParcel());

        job._lock = consumableModule.lock(job, consumable, 1);
        job.setTotalDuration(consumable.getInfo().consume.duration);
        job.setMainLabel("Consume " + consumable.getInfo().label);
        WorldHelper.getParcelAround(consumable.getParcel(), SurroundedPattern.SQUARE, job::addAcceptedParcel);

        job.addTask(new ActionTask("Consume", (character, hourInterval, localDateTime) -> {
            job.addProgression(hourInterval);
            callback.onConsume(consumable, job.getTotalDuration() - job.getDuration());
        }, () -> job.getDuration() >= job.getTotalDuration() ? TASK_COMPLETED : TASK_CONTINUE));

        job.addCloseTask(j -> {
            if (job._lock.available) {
                consumableModule.cancelLock(job._lock);
            }
            consumable.removeJob(job);
        });

        return job;
    }

}
