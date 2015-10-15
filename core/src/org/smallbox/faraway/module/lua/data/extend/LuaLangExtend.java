package org.smallbox.faraway.module.lua.data.extend;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.smallbox.faraway.game.model.GameData;
import org.smallbox.faraway.module.lua.DataExtendException;
import org.smallbox.faraway.module.lua.LuaModule;
import org.smallbox.faraway.module.lua.LuaModuleManager;
import org.smallbox.faraway.module.lua.data.LuaExtend;

/**
 * Created by Alex on 15/10/2015.
 */
public class LuaLangExtend extends LuaExtend {
    @Override
    public boolean accept(String type) {
        return "lang".equals(type);
    }

    @Override
    public void extend(LuaModuleManager luaModuleManager, LuaModule module, Globals globals, LuaValue value) throws DataExtendException {
        LuaValue luaStrings = value.get("strings");
        if (!luaStrings.isnil()) {
            for (int i = 1; i <= luaStrings.length(); i++) {
                GameData.getData().strings.put(luaStrings.get(i).get(1).toString().hashCode(), luaStrings.get(i).get(2).toString());
            }
        }
    }
}
