package org.smallbox.faraway.game.characterBuff.handler;

import org.smallbox.faraway.game.character.model.base.CharacterModel;

public abstract class BuffHandler {

    public int getLevel(CharacterModel character) {
        return OnGetLevel(character);
    }

    protected abstract int OnGetLevel(CharacterModel character);

}
