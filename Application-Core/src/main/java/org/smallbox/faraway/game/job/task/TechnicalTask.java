package org.smallbox.faraway.game.job.task;

import org.smallbox.faraway.game.job.JobTask;
import org.smallbox.faraway.game.job.taskAction.TechnicalTaskAction;

import static org.smallbox.faraway.game.job.JobTaskReturn.TASK_COMPLETED;

public class TechnicalTask extends JobTask {

    public TechnicalTask(TechnicalTaskAction technicalTaskAction) {
        super("Technical", TASK_COMPLETED, technicalTaskAction);
    }

}
