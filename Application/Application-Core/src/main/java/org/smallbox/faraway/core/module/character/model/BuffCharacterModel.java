package org.smallbox.faraway.core.module.character.model;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.game.model.ObjectModel;
import org.smallbox.faraway.core.module.character.model.base.CharacterModel;

/**
 * Created by Alex on 14/10/2015.
 */
public class BuffCharacterModel extends ObjectModel {
    public BuffInfo buff;
    public CharacterModel   character;
    public LuaValue         luaCharacter;
    public LuaValue         luaData;
    public String           message;
    public int              level;
    public int              mood;
    public long             startTick;
    public boolean          active;

    public BuffCharacterModel(BuffInfo buff, LuaValue luaCharacter, CharacterModel character) {
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
        if (tick % Application.APPLICATION_CONFIG.game.tickPerHour == 0) {
            this.buff.updateHourly(this, tick);
        }
    }

    public void check(int tick) {
        this.buff.check(this, tick);
    }
}
