package org.smallbox.faraway.game.item.job;

import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.game.item.UsableItem;
import org.smallbox.faraway.game.job.task.ActionTask;

import static org.smallbox.faraway.game.job.JobTaskReturn.TASK_COMPLETED;
import static org.smallbox.faraway.game.job.JobTaskReturn.TASK_CONTINUE;

@GameObject
public class UseJobFactory {

    public UseJob create(UsableItem item, OnUseCallback callback) {
        UseJob job = new UseJob(item.getParcel());

        job.setMainLabel("Use " + item.getInfo().label);
        job.addAcceptedParcel(item.getParcel());
        job.setTotalDuration(item.getInfo().use.duration);

        job.addTask(new ActionTask("Use", (character, hourInterval, localDateTime) -> {
            job.addProgression(hourInterval);
            callback.onUse(item, job.getTotalDuration() - job.getDuration());
        }, () -> job.getDuration() >= job.getTotalDuration() ? TASK_COMPLETED : TASK_CONTINUE));

        return job;
    }

}
