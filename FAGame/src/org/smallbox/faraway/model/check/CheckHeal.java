package org.smallbox.faraway.model.check;

import org.smallbox.faraway.manager.JobManager;
import org.smallbox.faraway.model.character.CharacterModel;
import org.smallbox.faraway.model.check.old.CharacterCheck;

/**
 * Created by Alex on 01/06/2015.
 */
public class CheckHeal extends CharacterCheck {

    public CheckHeal(CharacterModel character) {
        super(character);
    }

    @Override
    public boolean create(JobManager jobManager) {
        return false;
    }
}
