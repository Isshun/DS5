package org.smallbox.faraway.game.job.task;

import org.smallbox.faraway.game.job.JobTask;
import org.smallbox.faraway.game.job.JobTaskReturn;

import java.time.LocalDateTime;
import java.util.function.Supplier;

public class ActionTask extends JobTask {
    private final Supplier<JobTaskReturn> jobTaskReturnSupplier;

    public ActionTask(String label, JobTaskAction action, Supplier<JobTaskReturn> jobTaskReturnSupplier) {
        super(label, action);
        this.jobTaskReturnSupplier = jobTaskReturnSupplier;
    }

    @Override
    public JobTaskReturn onGetStatus(LocalDateTime localDateTime) {
        return jobTaskReturnSupplier.get();
    }
}
