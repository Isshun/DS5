package org.smallbox.faraway.modules.characterBuff;

import org.smallbox.faraway.modules.character.model.base.CharacterModel;

public abstract class BuffFactory {
    protected abstract BuffModel build();
    protected abstract boolean check(CharacterModel character);
    protected abstract String name();
}
