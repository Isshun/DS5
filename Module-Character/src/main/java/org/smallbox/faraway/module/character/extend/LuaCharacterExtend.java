package org.smallbox.faraway.module.character.extend;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.smallbox.faraway.core.engine.module.ModuleBase;
import org.smallbox.faraway.core.engine.module.lua.data.DataExtendException;
import org.smallbox.faraway.core.engine.module.lua.data.LuaExtend;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.core.game.modelInfo.CharacterInfo;

/**
 * Created by Alex on 22/11/2015.
 */
public class LuaCharacterExtend extends LuaExtend {
    @Override
    public boolean accept(String type) {
        return "character".equals(type);
    }

    @Override
    public void extend(ModuleBase module, Globals globals, LuaValue value) throws DataExtendException {
        String name = getString(value, "name", null);
        CharacterInfo characterInfo = Data.getData().characters.get(name);
        if (characterInfo == null) {
            characterInfo = new CharacterInfo(name);
            Data.getData().characters.put(name, characterInfo);
        }

        characterInfo.path = "data/characters/human.png";

        LuaValue needs = value.get("needs");
        if (!needs.isnil()) {
            setNeed(characterInfo.needs.energy, needs.get("energy"));
            setNeed(characterInfo.needs.food, needs.get("food"));
            setNeed(characterInfo.needs.water, needs.get("water"));
            setNeed(characterInfo.needs.oxygen, needs.get("oxygen"));
            setNeed(characterInfo.needs.relation, needs.get("relation"));
            setNeed(characterInfo.needs.joy, needs.get("joy"));
        }
    }

    private void setNeed(CharacterInfo.NeedInfo need, LuaValue needValue) {
        if (!needValue.isnil()) {
            need.warning = needValue.get("warning").optint(50);
            need.critical = needValue.get("critical").optint(25);

            LuaValue change = needValue.get("change");
            need.change.work = !change.isnil() ? change.get("work").optdouble(0) : 0;
            need.change.sleep = !change.isnil() ? change.get("sleep").optdouble(0) : 0;
            need.change.rest = !change.isnil() ? change.get("rest").optdouble(0) : 0;
        }
    }
}