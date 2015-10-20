package org.smallbox.faraway.core.game.model.check.joy;

import org.smallbox.faraway.core.game.model.character.base.CharacterModel;
import org.smallbox.faraway.core.game.model.check.old.CharacterCheck;
import org.smallbox.faraway.core.game.model.job.abs.JobModel;

/**
 * Created by Alex on 17/06/2015.
 */
public class CheckJoySleep extends CharacterCheck {
    @Override
    public JobModel create(CharacterModel character) {
        return null;
    }

    @Override
    public boolean check(CharacterModel character) {
        return false;
    }

    @Override
    public boolean need(CharacterModel character) {
        return character.getNeeds().energy < character.getType().needs.energy.critical;
    }
}
