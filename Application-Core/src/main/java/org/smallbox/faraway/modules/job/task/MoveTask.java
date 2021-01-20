package org.smallbox.faraway.modules.job.task;

import org.smallbox.faraway.modules.job.JobModel;
import org.smallbox.faraway.modules.job.JobTask;

import static org.smallbox.faraway.modules.job.JobTaskReturn.TASK_COMPLETED_STOP;

public class MoveTask extends JobTask {

    public MoveTask(String label, JobModel.ParcelCallback parcelCallback) {
        super(label, TASK_COMPLETED_STOP, job -> job._targetParcel = parcelCallback.getParcel());
    }

}
