package org.smallbox.faraway.modules.job.freeTimeJobs;

import org.smallbox.faraway.modules.character.model.AndroidModel;
import org.smallbox.faraway.modules.character.model.CharacterSkillExtra;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.job.JobCheckReturn;
import org.smallbox.faraway.modules.job.JobModel;
import org.smallbox.faraway.modules.job.task.ActionTask;

import static org.smallbox.faraway.modules.job.JobTaskReturn.TASK_COMPLETED;
import static org.smallbox.faraway.modules.job.JobTaskReturn.TASK_CONTINUE;

public class BasicAndroidSelfCheckJob extends JobModel {

    private double _time;

    public BasicAndroidSelfCheckJob(CharacterModel character) {
        setMainLabel("BasicAndroidSelfCheckJob");
        setVisible(false);

        addTask(new ActionTask("BasicAndroidSelfCheckJob", (character1, hourInterval, localDateTime) -> _time += hourInterval, () -> _time > 1 ? TASK_COMPLETED : TASK_CONTINUE));
    }

    @Override
    protected JobCheckReturn onCheck(CharacterModel character) {
        return JobCheckReturn.OK;
    }

    @Override
    public boolean checkCharacterAccepted(CharacterModel character) {
        return character instanceof AndroidModel;
    }

    @Override
    public CharacterSkillExtra.SkillType getSkillType() {
        return null;
    }

}
