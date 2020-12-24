package org.smallbox.faraway.modules.job;

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
