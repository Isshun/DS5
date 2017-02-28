package org.smallbox.faraway.core.module.job.model.abs;

import org.smallbox.faraway.core.module.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.job.JobTask;
import org.smallbox.faraway.modules.job.JobTaskReturn;

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
                return JobTaskReturn.COMPLETE;
            });
            this.label = label;
            this.action = action;
        }

}
