package org.smallbox.faraway.core.game.model.characterBuff.handler;

import org.smallbox.faraway.modules.character.model.base.CharacterModel;

/**
 * Created by Alex on 10/03/2017.
 */
public abstract class BuffHandler {

    public int getLevel(CharacterModel character) {
        return OnGetLevel(character);
    }

    protected abstract int OnGetLevel(CharacterModel character);

}
