package org.smallbox.faraway.game.model.check.joy;

import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.check.old.CharacterCheck;
import org.smallbox.faraway.game.model.job.JobModel;

/**
 * Created by Alex on 17/06/2015.
 */
public class CheckJoyTalk extends CharacterCheck {
    @Override
    public JobModel create(CharacterModel character) {
        return null;
    }

    @Override
    public boolean check(CharacterModel character) {
        return false;
    }
}
