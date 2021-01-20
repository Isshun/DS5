package org.smallbox.faraway.modules.job.task;

import org.smallbox.faraway.modules.job.JobTask;
import org.smallbox.faraway.modules.job.taskAction.TechnicalTaskAction;

import static org.smallbox.faraway.modules.job.JobTaskReturn.TASK_COMPLETED;

public class TechnicalTask extends JobTask {

    public TechnicalTask(TechnicalTaskAction technicalTaskAction) {
        super("Technical", TASK_COMPLETED, technicalTaskAction);
    }

}
