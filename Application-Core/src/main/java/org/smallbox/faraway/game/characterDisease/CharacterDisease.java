package org.smallbox.faraway.game.characterDisease;

import org.luaj.vm2.LuaValue;
import org.smallbox.faraway.game.character.model.base.CharacterModel;

public class CharacterDisease {
    public DiseaseInfo      info;
    public CharacterModel   character;
    public LuaValue         luaCharacter;
    public LuaValue         luaData;
    public String           message;
    public int              level;

    public CharacterDisease(DiseaseInfo info, CharacterModel character) {
        this.info = info;
        this.character = character;
    }

    public void start() {
        if (this.character.isAlive()) {
            this.info.start(this);
        }
    }

    public void update(int tick) {
        if (this.character.isAlive()) {
            this.info.update(this, tick);
        }
    }
}
