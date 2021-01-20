package org.smallbox.faraway.modules.job.freeTimeJobs;

import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.module.world.model.Parcel;
import org.smallbox.faraway.modules.character.model.CharacterSkillExtra;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.job.JobCheckReturn;
import org.smallbox.faraway.modules.job.JobModel;
import org.smallbox.faraway.modules.job.task.MoveTask;

public class BasicWalkJob extends JobModel {

    public BasicWalkJob(CharacterModel character) {
        setMainLabel("Walk");
        setVisible(false);
        Parcel targetParcel = WorldHelper.getRandomParcel(character.getParcel(), 32);
        addTask(new MoveTask("move 1", () -> targetParcel));
    }

    @Override
    protected JobCheckReturn onCheck(CharacterModel character) {
        return JobCheckReturn.OK;
    }

    @Override
    public boolean checkCharacterAccepted(CharacterModel character) {
        return true;
    }

    @Override
    public CharacterSkillExtra.SkillType getSkillType() {
        return null;
    }

}
