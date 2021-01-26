package org.smallbox.faraway.game.job.freeTimeJobs;

import org.smallbox.faraway.game.character.model.CharacterSkillExtra;
import org.smallbox.faraway.game.character.model.base.CharacterModel;
import org.smallbox.faraway.game.job.JobCheckReturn;
import org.smallbox.faraway.game.job.JobModel;
import org.smallbox.faraway.game.world.WorldHelper;

public class BasicWalkJob extends JobModel {

    public BasicWalkJob(CharacterModel character) {
        super(character.getParcel());
        setMainLabel("Walk");
        setVisible(false);
        addAcceptedParcel(WorldHelper.getRandomParcel(character.getParcel(), 32));
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
