package org.smallbox.faraway.game.model.check;

import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.check.old.CharacterCheck;
import org.smallbox.faraway.game.model.job.BaseJobModel;

/**
 * Created by Alex on 05/06/2015.
 */
public class CheckCharacterUse extends CharacterCheck {

    @Override
    public BaseJobModel create(CharacterModel character) {
        return null;
    }

    @Override
    public boolean check(CharacterModel character) {
        return false;
    }

    @Override
    public boolean need(CharacterModel character) {
        return false;
    }
}
