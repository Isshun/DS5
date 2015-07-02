package org.smallbox.faraway.engine.lua;

import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.character.base.CharacterNeeds;
import org.smallbox.faraway.game.model.character.base.CharacterStats;

/**
 * Created by Alex on 19/06/2015.
 */
public class LuaCharacterModel {
    public final int                id;
    public final String             name;
    public final CharacterNeeds     needs;
    public final CharacterStats     stats;
    public final CharacterModel     character;

    public LuaCharacterModel(CharacterModel character) {
        this.id = character.getId();
        this.name = character.getInfo().getName();
        this.needs = character.getNeeds();
        this.stats = character.getStats();
        this.character = character;
    }

    public boolean isAlive() {
        return this.character.isAlive();
    }
}
