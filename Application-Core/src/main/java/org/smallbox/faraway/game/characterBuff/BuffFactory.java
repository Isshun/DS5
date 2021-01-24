package org.smallbox.faraway.game.characterBuff;

import org.smallbox.faraway.game.character.model.base.CharacterModel;

public abstract class BuffFactory {
    protected abstract BuffModel build();
    protected abstract boolean check(CharacterModel character);
    protected abstract String name();
}
