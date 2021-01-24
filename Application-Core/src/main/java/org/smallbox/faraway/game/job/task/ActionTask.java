package org.smallbox.faraway.game.job.task;

import org.smallbox.faraway.game.job.JobTask;
import org.smallbox.faraway.game.job.JobTaskReturn;

import java.util.function.Supplier;

public class ActionTask extends JobTask {

    public ActionTask(String label, JobTaskAction action, Supplier<JobTaskReturn> jobTaskReturnSupplier) {
        super(label, action);
    }

}
