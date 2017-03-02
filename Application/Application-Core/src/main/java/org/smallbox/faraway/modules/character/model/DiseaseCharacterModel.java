package org.smallbox.faraway.modules.character.model;

import org.luaj.vm2.LuaValue;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;

/**
 * Created by Alex on 26/10/2015.
 */
public class DiseaseCharacterModel {
    public DiseaseInfo disease;
    public CharacterModel   character;
    public LuaValue         luaCharacter;
    public LuaValue         luaData;
    public String           message;
    public int              level;

    public DiseaseCharacterModel(DiseaseInfo disease, LuaValue luaCharacter, CharacterModel character, LuaValue data) {
        this.disease = disease;
        this.luaData = data;
//        this.luaData = new LuaTable();
        this.luaCharacter = luaCharacter;
        this.character = character;
    }

    public void start() {
        if (this.character.isAlive()) {
            this.disease.start(this);
        }
    }

    public void update(int tick) {
        if (this.character.isAlive()) {
            this.disease.update(this, tick);
        }
    }
}
