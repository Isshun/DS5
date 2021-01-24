package org.smallbox.faraway.game.job.freeTimeJobs;

import org.smallbox.faraway.game.character.model.AndroidModel;
import org.smallbox.faraway.game.character.model.CharacterSkillExtra;
import org.smallbox.faraway.game.character.model.base.CharacterModel;
import org.smallbox.faraway.game.job.JobCheckReturn;
import org.smallbox.faraway.game.job.JobModel;
import org.smallbox.faraway.game.job.task.ActionTask;

import static org.smallbox.faraway.game.job.JobTaskReturn.TASK_COMPLETED;
import static org.smallbox.faraway.game.job.JobTaskReturn.TASK_CONTINUE;

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
