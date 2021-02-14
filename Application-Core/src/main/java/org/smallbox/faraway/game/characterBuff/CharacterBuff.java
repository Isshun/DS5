package org.smallbox.faraway.game.characterBuff;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.smallbox.faraway.core.dependencyInjector.DependencyManager;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.game.character.model.base.CharacterModel;
import org.smallbox.faraway.game.world.ObjectModel;

public class CharacterBuff extends ObjectModel {
    public BuffInfo         info;
    public CharacterModel   character;
    public LuaValue         luaCharacter;
    public LuaValue         luaData;
    public String           message;
    public int              level;
    public BuffInfo.BuffLevelInfo levelInfo;
    public int              mood;
    public long             startTick;
    public boolean          active;

    public CharacterBuff(BuffInfo buffInfo, CharacterModel character) {
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
        if (tick % DependencyManager.getInstance().getDependency(Game.class).getTickPerHour() == 0) {
            this.info.updateHourly(this, tick);
        }
    }

    public void check(int tick) {
        this.info.check(this, tick);
    }
}
