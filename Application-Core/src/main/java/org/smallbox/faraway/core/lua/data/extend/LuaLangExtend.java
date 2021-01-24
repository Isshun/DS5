package org.smallbox.faraway.core.lua.data.extend;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.module.ModuleBase;
import org.smallbox.faraway.core.lua.data.DataExtendException;
import org.smallbox.faraway.core.lua.data.LuaExtend;
import org.smallbox.faraway.core.game.DataManager;

import java.io.File;

@ApplicationObject
public class LuaLangExtend extends LuaExtend {

    @Override
    public boolean accept(String type) {
        return "lang".equals(type);
    }

    @Override
    public void extend(DataManager dataManager, ModuleBase module, Globals globals, LuaValue value, File dataDirectory) throws DataExtendException {
        LuaValue luaStrings = value.get("strings");
        if (!luaStrings.isnil()) {
            for (int i = 1; i <= luaStrings.length(); i++) {
                String str1 = luaStrings.get(i).get(1).toString();
                String str2 = luaStrings.get(i).get(2).toString();

                // Exact case
                dataManager.strings.put(str1.hashCode(), str2);

                // Lower case
                if (!str1.toLowerCase().equals(str1)) {
                    dataManager.strings.put(str1.toLowerCase().hashCode(), str2.toLowerCase());
                }

                // Capitalized
                dataManager.strings.put(
                        (Character.toUpperCase(str1.toLowerCase().charAt(0)) + str1.substring(1)).hashCode(),
                        Character.toUpperCase(str2.toLowerCase().charAt(0)) + str2.substring(1));
            }
        }
    }
}
