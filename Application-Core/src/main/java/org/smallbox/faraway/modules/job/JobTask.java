package org.smallbox.faraway.modules.job;

import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.job.taskAction.TechnicalTaskAction;

import java.time.LocalDateTime;

public class JobTask implements JobInterface {
    public final String label;
    public JobTaskAction action;
    public final TechnicalTaskAction technicalAction;
    public final JobTaskReturn taskReturn;
    public LocalDateTime startTime;

    public void init(LocalDateTime localDateTime) {
        startTime = localDateTime;
        onInit(localDateTime);
    }

    public void action(CharacterModel character, double hourInterval, LocalDateTime localDateTime) {
        if (this.action != null) {
            this.action.onExecuteTask(character, hourInterval, localDateTime);
        }

        onAction(character, hourInterval, localDateTime);
    }

    public JobTaskReturn getStatus(CharacterModel character, double hourInterval, LocalDateTime localDateTime) {
        return onGetStatus(localDateTime);
    }

    public interface JobTaskAction {
        void onExecuteTask(CharacterModel character, double hourInterval, LocalDateTime localDateTime);
    }

    public JobTask(String label, JobTaskAction action) {
        this.label = label;
        this.action = action;
        this.technicalAction = null;
        this.taskReturn = null;
    }

    public JobTask(String label, JobTaskReturn taskReturn, TechnicalTaskAction technicalAction) {
        this.label = label;
        this.action = null;
        this.technicalAction = technicalAction;
        this.taskReturn = taskReturn;
    }

    @Override
    public String toString() {
        return this.label;
    }

}
