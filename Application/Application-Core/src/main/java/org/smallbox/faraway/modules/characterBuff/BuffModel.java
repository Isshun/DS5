package org.smallbox.faraway.modules.characterBuff;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.game.model.ObjectModel;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;

/**
 * Created by Alex on 14/10/2015.
 */
public class BuffModel extends ObjectModel {
    public BuffInfo info;
    public CharacterModel   character;
    public LuaValue         luaCharacter;
    public LuaValue         luaData;
    public String           message;
    public int              level;
    public BuffInfo.BuffLevelInfo levelInfo;
    public int              mood;
    public long             startTick;
    public boolean          active;

    public BuffModel(BuffInfo buffInfo, CharacterModel character) {
        this.info = buffInfo;
        this.luaData = new LuaTable();
        this.luaCharacter = null;
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
        if (tick % Application.config.game.tickPerHour == 0) {
            this.info.updateHourly(this, tick);
        }
    }

    public void check(int tick) {
        this.info.check(this, tick);
    }
}
