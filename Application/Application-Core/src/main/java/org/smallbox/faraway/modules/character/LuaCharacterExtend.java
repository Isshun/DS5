package org.smallbox.faraway.modules.character;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.engine.module.ModuleBase;
import org.smallbox.faraway.core.engine.module.lua.data.DataExtendException;
import org.smallbox.faraway.core.engine.module.lua.data.LuaExtend;
import org.smallbox.faraway.core.game.modelInfo.CharacterInfo;

import java.io.File;

/**
 * Created by Alex on 22/11/2015.
 */
public class LuaCharacterExtend extends LuaExtend {

    @Override
    public boolean accept(String type) {
        return "character".equals(type);
    }

    @Override
    public void extend(ModuleBase module, Globals globals, LuaValue value, File dataDirectory) throws DataExtendException {
        String name = getString(value, "name", null);
        CharacterInfo characterInfo = Application.data.characters.computeIfAbsent(name, k -> new CharacterInfo(name));

        characterInfo.path = "data/characters/human.png";

        LuaValue needs = value.get("needs");
        if (!needs.isnil()) {
            setNeed(characterInfo.needs.energy, needs.get("energy"));
            setNeed(characterInfo.needs.food, needs.get("food"));
            setNeed(characterInfo.needs.drink, needs.get("drink"));
            setNeed(characterInfo.needs.oxygen, needs.get("oxygen"));
            setNeed(characterInfo.needs.relation, needs.get("relation"));
            setNeed(characterInfo.needs.entertainment, needs.get("entertainment"));
        }
    }

    private void setNeed(CharacterInfo.NeedInfo need, LuaValue needValue) {
        if (!needValue.isnil()) {
            need.warning = needValue.get("warning").optdouble(0.5);
            need.critical = needValue.get("critical").optdouble(0.25);
            need.optimal = needValue.get("optimal").optdouble(1);

            LuaValue change = needValue.get("change");
            need.change.work = !change.isnil() ? change.get("work").optdouble(0) / Application.config.game.tickPerHour : 0;
            need.change.sleep = !change.isnil() ? change.get("sleep").optdouble(0) / Application.config.game.tickPerHour : 0;
            need.change.rest = !change.isnil() ? change.get("rest").optdouble(0) / Application.config.game.tickPerHour : 0;
        }
    }

}