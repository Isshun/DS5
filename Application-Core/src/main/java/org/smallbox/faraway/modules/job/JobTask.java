package org.smallbox.faraway.modules.job;

import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.job.taskAction.TechnicalTaskAction;

public class JobTask {

    public final String label;
    public final JobTaskAction action;
    public final TechnicalTaskAction technicalAction;

    public interface JobTaskAction {
        JobTaskReturn onExecuteTask(CharacterModel character, double hourInterval);
    }

    public JobTask(String label, JobTaskAction action) {
        this.label = label;
        this.action = action;
        this.technicalAction = null;
    }

    public JobTask(String label, TechnicalTaskAction technicalAction) {
        this.label = label;
        this.action = null;
        this.technicalAction = technicalAction;
    }

    @Override
    public String toString() {
        return this.label;
    }

}
