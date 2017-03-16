package org.smallbox.faraway.modules.character.job;

import org.smallbox.faraway.core.module.job.check.old.CharacterCheck;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.job.JobModel;

/**
 * Created by Alex on 05/06/2015.
 */
public class CheckCharacterUse extends CharacterCheck {

    @Override
    public JobModel onCreateJob(CharacterModel character) {
        return null;
    }

    @Override
    public boolean isJobLaunchable(CharacterModel character) {
        return false;
    }

    @Override
    public boolean isJobNeeded(CharacterModel character) {
        return false;
    }
}
