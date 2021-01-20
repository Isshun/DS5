package org.smallbox.faraway.modules.job.taskAction;

import org.smallbox.faraway.modules.job.JobModel;

public interface TechnicalTaskAction {
    void onExecuteTask(JobModel job);
}
