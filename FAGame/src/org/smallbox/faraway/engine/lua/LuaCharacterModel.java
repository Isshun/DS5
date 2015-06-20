package org.smallbox.faraway.engine.lua;

import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.model.character.base.CharacterModel;

/**
 * Created by Alex on 19/06/2015.
 */
public class LuaCharacterModel {
    public final int        id;
    public final String     name;
    public final CharacterModel character;

    public LuaCharacterModel(CharacterModel character) {
        this.id = character.getId();
        this.name = character.getName();
        this.character = character;
    }

    public boolean isAlive() {
        return this.character.isAlive();
    }
}
