package org.smallbox.faraway.game.item.job;

import org.smallbox.faraway.game.character.model.CharacterSkillExtra;
import org.smallbox.faraway.game.character.model.base.CharacterModel;
import org.smallbox.faraway.game.job.JobCheckReturn;
import org.smallbox.faraway.game.job.JobModel;
import org.smallbox.faraway.game.world.Parcel;

public class UseJob extends JobModel {

    public UseJob(Parcel parcel) {
        super(parcel);
    }

    @Override
    public JobCheckReturn onCheck(CharacterModel character) {
        // TODO
//        // Item is no longer exists
//        if (_item != _item.getParcel().getItem()) {
//            _reason = JobAbortReason.TASK_ERROR;
//            return JobCheckReturn.ABORT;
//        }

//        if (!DependencyInjector.getInstance().getDependency(PathManager.class).hasPath(character.getParcel(), _item.getParcel())) {
//            return JobCheckReturn.STAND_BY;
//        }

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
