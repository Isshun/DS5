package org.smallbox.faraway.game.job.task;

import org.smallbox.faraway.core.game.GameTime;
import org.smallbox.faraway.game.character.model.base.CharacterModel;
import org.smallbox.faraway.game.job.JobTask;
import org.smallbox.faraway.game.job.JobTaskReturn;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

public class ActionDurationTask extends JobTask {
    private final TimeUnit timeUnit;
    private final int durationValue;
    private LocalDateTime endTime;

    public ActionDurationTask(String label, int durationValue, TimeUnit timeUnit, JobTaskAction action) {
        super(label, action);
        this.timeUnit = timeUnit;
        this.durationValue = durationValue;
    }

    @Override
    public void onInit(LocalDateTime localDateTime) {
        endTime = GameTime.plus(localDateTime, durationValue, timeUnit);
    }

    @Override
    public JobTaskReturn getStatus(CharacterModel character, double hourInterval, LocalDateTime localDateTime) {
        return localDateTime.isBefore(endTime) ? JobTaskReturn.TASK_CONTINUE : JobTaskReturn.TASK_COMPLETED;
    }

}
