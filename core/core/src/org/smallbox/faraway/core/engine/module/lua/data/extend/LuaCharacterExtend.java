package org.smallbox.faraway.core.engine.module.lua.data.extend;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.smallbox.faraway.core.engine.module.lua.LuaModule;
import org.smallbox.faraway.core.engine.module.lua.LuaModuleManager;
import org.smallbox.faraway.core.engine.module.lua.data.DataExtendException;
import org.smallbox.faraway.core.engine.module.lua.data.LuaExtend;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.core.game.modelInfo.CharacterInfo;
import org.smallbox.faraway.core.game.modelInfo.QuestInfo;
import org.smallbox.faraway.core.game.module.character.model.HumanModel;

/**
 * Created by Alex on 22/11/2015.
 */
public class LuaCharacterExtend extends LuaExtend {
    @Override
    public boolean accept(String type) {
        return "character".equals(type);
    }

    @Override
    public void extend(LuaModuleManager luaModuleManager, LuaModule module, Globals globals, LuaValue value) throws DataExtendException {
        String name = getString(value, "name", null);
        CharacterInfo character = Data.getData().characters.get(name);
        if (character == null) {
            character = new CharacterInfo(name);
            Data.getData().characters.put(name, character);
        }

        character.path = "data/characters/human.png";

        character.needs = new CharacterInfo.Needs();
        character.needs.heat = new CharacterInfo.NeedInfo();
        character.needs.heat.optimal = 38;
        character.needs.heat.change = new CharacterInfo.ChangeInfo();

        character.needs.energy = new CharacterInfo.NeedInfo();
        character.needs.energy.change = new CharacterInfo.ChangeInfo();
        character.needs.food = new CharacterInfo.NeedInfo();
        character.needs.food.change = new CharacterInfo.ChangeInfo();
        character.needs.happiness = new CharacterInfo.NeedInfo();
        character.needs.happiness.change = new CharacterInfo.ChangeInfo();
        character.needs.joy = new CharacterInfo.NeedInfo();
        character.needs.joy.change = new CharacterInfo.ChangeInfo();
        character.needs.oxygen = new CharacterInfo.NeedInfo();
        character.needs.oxygen.change = new CharacterInfo.ChangeInfo();
        character.needs.relation = new CharacterInfo.NeedInfo();
        character.needs.relation.change = new CharacterInfo.ChangeInfo();
        character.needs.water = new CharacterInfo.NeedInfo();
        character.needs.water.change = new CharacterInfo.ChangeInfo();
    }
}