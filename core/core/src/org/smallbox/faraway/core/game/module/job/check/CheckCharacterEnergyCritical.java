package org.smallbox.faraway.core.game.module.job.check;

import org.smallbox.faraway.core.game.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.game.module.job.SleepJob;
import org.smallbox.faraway.core.game.module.job.check.old.CharacterCheck;
import org.smallbox.faraway.core.game.module.job.model.abs.JobModel;

/**
 * Created by Alex on 25/10/2015.
 */
public class CheckCharacterEnergyCritical extends CharacterCheck {
    @Override
    public JobModel create(CharacterModel character) {
        return new SleepJob();
    }

    @Override
    public boolean check(CharacterModel character) {
        return true;
    }

    @Override
    public boolean need(CharacterModel character) {
        return character.getNeeds().energy < character.getType().needs.energy.critical;
    }
}
