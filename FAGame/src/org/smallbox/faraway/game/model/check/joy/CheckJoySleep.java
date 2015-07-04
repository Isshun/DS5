package org.smallbox.faraway.game.model.check.joy;

import org.smallbox.faraway.game.model.GameData;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.check.old.CharacterCheck;
import org.smallbox.faraway.game.model.job.BaseJobModel;

/**
 * Created by Alex on 17/06/2015.
 */
public class CheckJoySleep extends CharacterCheck {
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
        return character.getNeeds().energy < character.getType().needs.energy.critical;
    }
}
