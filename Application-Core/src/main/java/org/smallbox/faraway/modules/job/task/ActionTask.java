package org.smallbox.faraway.modules.job.task;

import org.smallbox.faraway.modules.job.JobTask;
import org.smallbox.faraway.modules.job.JobTaskReturn;

import java.util.function.Supplier;

public class ActionTask extends JobTask {

    public ActionTask(String label, JobTaskAction action, Supplier<JobTaskReturn> jobTaskReturnSupplier) {
        super(label, action);
    }

}
