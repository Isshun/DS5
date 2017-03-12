package org.smallbox.faraway.modules.job;

import org.smallbox.faraway.modules.character.model.base.CharacterModel;

/**
 * Created by Alex on 28/02/2017.
 */
public class JobTechnicalTask extends JobTask {

    public interface JobTechnicalTaskAction {
        void onExecuteTask(CharacterModel character);
    }

    public String label;
    public JobTechnicalTaskAction action;

    public JobTechnicalTask(String label, JobTechnicalTaskAction action) {
        super(label, character -> {
            action.onExecuteTask(character);
            return JobTaskReturn.TASK_COMPLETE;
        });
        this.label = label;
        this.action = action;
    }

}
