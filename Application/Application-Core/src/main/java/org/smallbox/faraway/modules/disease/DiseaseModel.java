package org.smallbox.faraway.modules.disease;

import org.luaj.vm2.LuaValue;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;

/**
 * Created by Alex on 26/10/2015.
 */
public class DiseaseModel {
    public DiseaseInfo      info;
    public CharacterModel   character;
    public LuaValue         luaCharacter;
    public LuaValue         luaData;
    public String           message;
    public int              level;

    public DiseaseModel(DiseaseInfo info, CharacterModel character) {
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
