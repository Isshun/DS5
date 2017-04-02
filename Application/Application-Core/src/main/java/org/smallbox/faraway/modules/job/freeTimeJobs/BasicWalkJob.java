package org.smallbox.faraway.modules.job.freeTimeJobs;

import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.job.JobModel;

/**
 * Created by Alex on 14/03/2017.
 */
public class BasicWalkJob extends JobModel {

    public BasicWalkJob(CharacterModel character) {
        setMainLabel("Walk");
        setVisible(false);
        ParcelModel targetParcel = WorldHelper.getRandomParcel(character.getParcel(), 32);
        addMoveTask("move 1", targetParcel);
    }

    @Override
    protected JobCheckReturn onCheck(CharacterModel character) {
        return JobCheckReturn.OK;
    }

    @Override
    public boolean checkCharacterAccepted(CharacterModel character) {
        return true;
    }

}
