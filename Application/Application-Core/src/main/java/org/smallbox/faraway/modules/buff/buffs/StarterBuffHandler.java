package org.smallbox.faraway.modules.buff.buffs;

import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.modules.buff.BuffHandler;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;

/**
 * Created by Alex on 10/03/2017.
 */
public class StarterBuffHandler extends BuffHandler {

    @Override
    protected int OnGetLevel(CharacterModel character) {
        int day = Application.gameManager.getGame().getDay();

        if (day < 10) {
            return 2;
        }

        if (day < 20) {
            return 1;
        }

        return 0;
    }
}
