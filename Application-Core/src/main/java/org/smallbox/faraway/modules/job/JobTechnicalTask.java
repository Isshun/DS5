package org.smallbox.faraway.modules.job;

/**
 * Created by Alex on 28/02/2017.
 */
public class JobTechnicalTask extends JobTask {

    public interface JobTechnicalTaskAction {
        void onExecuteTask();
    }

    public String label;
    public JobTechnicalTaskAction action;

    public JobTechnicalTask(JobTechnicalTaskAction action) {
        super("technical", (character, hourInterval) -> {
            action.onExecuteTask();
            return JobTaskReturn.TASK_COMPLETE;
        });
        this.label = label;
        this.action = action;
    }

}
