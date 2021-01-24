package org.smallbox.faraway.game.job.task;

import org.smallbox.faraway.game.job.JobModel;
import org.smallbox.faraway.game.job.JobTask;

import static org.smallbox.faraway.game.job.JobTaskReturn.TASK_COMPLETED_STOP;

public class MoveTask extends JobTask {

    public MoveTask(String label, JobModel.ParcelCallback parcelCallback) {
        super(label, TASK_COMPLETED_STOP, job -> job._targetParcel = parcelCallback.getParcel());
    }

}
