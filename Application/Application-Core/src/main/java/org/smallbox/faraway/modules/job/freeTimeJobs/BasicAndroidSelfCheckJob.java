package org.smallbox.faraway.modules.job.freeTimeJobs;

import org.smallbox.faraway.modules.character.model.CharacterSkillExtra;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.job.JobModel;
import org.smallbox.faraway.modules.job.JobTaskReturn;

/**
 * Created by Alex on 14/03/2017.
 */
public class BasicAndroidSelfCheckJob extends JobModel {

    private double _time;

    public BasicAndroidSelfCheckJob(CharacterModel character) {
        setMainLabel("BasicAndroidSelfCheckJob");
        setVisible(false);

        addTask("BasicAndroidSelfCheckJob", (character1, hourInterval) -> (_time += hourInterval) > 1 ? JobTaskReturn.TASK_COMPLETE : JobTaskReturn.TASK_CONTINUE);
    }

    @Override
    protected JobCheckReturn onCheck(CharacterModel character) {
        return JobCheckReturn.OK;
    }

    @Override
    public CharacterSkillExtra.SkillType getSkillNeeded() {
        return null;
    }

}
