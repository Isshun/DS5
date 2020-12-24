package org.smallbox.faraway.modules.job;

import org.smallbox.faraway.modules.character.model.base.CharacterModel;

public class JobTask {

    public interface JobTaskAction {
        JobTaskReturn onExecuteTask(CharacterModel character, double hourInterval);
    }

    public String label;
    public JobTaskAction action;

    public JobTask(String label, JobTaskAction action) {
        this.label = label;
        this.action = action;
    }

    @Override
    public String toString() {
        return this.label;
    }

}
