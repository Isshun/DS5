package org.smallbox.faraway.game.model.character;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.smallbox.faraway.game.model.ObjectModel;
import org.smallbox.faraway.game.model.character.base.CharacterModel;

/**
 * Created by Alex on 14/10/2015.
 */
public class BuffCharacterModel extends ObjectModel {
    public BuffModel        buff;
    public CharacterModel   character;
    public LuaValue         luaCharacter;
    public LuaValue         luaData;
    public String           message;
    public int              level;
    public int              mood;

    public BuffCharacterModel(BuffModel buff, LuaValue luaCharacter, CharacterModel character) {
        this.buff = buff;
        this.luaData = new LuaTable();
        this.luaCharacter = luaCharacter;
        this.character = character;
    }

    public void start() {
        if (this.character.isAlive()) {
            this.buff.start(this);
        }
    }

    public void update(int tick) {
        if (this.character.isAlive()) {
            this.buff.update(this, tick);
        }
    }
}
