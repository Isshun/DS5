package org.smallbox.faraway.core.module.job.check.old;

import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.job.JobModel;
import org.smallbox.faraway.util.Log;

public abstract class CharacterCheck {
    protected abstract JobModel onCreateJob(CharacterModel character);
    protected abstract boolean isJobLaunchable(CharacterModel character);
    protected abstract boolean isJobNeeded(CharacterModel character);

    public String getLabel() {
        return getClass().getName();
    }

    public boolean checkJobNeeded(CharacterModel character) {
        if (isJobNeeded(character)) {
            if (!character.hasNeed(this)) {
                character.addNeed(this);
                Log.info("Need (%s) added to character (%s)", this.getClass().getName(), character.getName());
            }
            return true;
        } else {
            if (character.hasNeed(this)) {
                character.removeNeed(this);
                Log.info("Need (%s) removed from character (%s)", this.getClass().getName(), character.getName());
            }
            return false;
        }
    }

    public boolean checkJobLaunchable(CharacterModel character) {
        return isJobLaunchable(character);
    }

    public JobModel createJob(CharacterModel character) {
        return onCreateJob(character);
    }
}
