package org.smallbox.faraway.modules.characterBuff.handler;

import org.smallbox.faraway.modules.character.model.base.CharacterModel;

public abstract class BuffHandler {

    public int getLevel(CharacterModel character) {
        return OnGetLevel(character);
    }

    protected abstract int OnGetLevel(CharacterModel character);

}
