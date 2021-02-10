package org.smallbox.faraway.game.job.task;

import org.smallbox.faraway.game.job.JobTask;
import org.smallbox.faraway.game.job.JobTaskReturn;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Supplier;

import static org.smallbox.faraway.game.job.JobTaskReturn.TASK_COMPLETED;
import static org.smallbox.faraway.game.job.JobTaskReturn.TASK_CONTINUE;

public class ActionTask extends JobTask {
    private final Supplier<JobTaskReturn> jobTaskReturnSupplier;

    public ActionTask(String label, JobTaskAction action, Supplier<JobTaskReturn> jobTaskReturnSupplier) {
        super(label, action);
        this.jobTaskReturnSupplier = jobTaskReturnSupplier;
    }

    public ActionTask(String label, JobTaskAction action) {
        super(label, action);
        this.jobTaskReturnSupplier = null;
    }

    @Override
    public JobTaskReturn onGetStatus(LocalDateTime localDateTime) {
        return Optional.ofNullable(jobTaskReturnSupplier).map(Supplier::get).orElse(job.getDuration() >= job.getTotalDuration() ? TASK_COMPLETED : TASK_CONTINUE);
    }
}
