package org.smallbox.faraway.lua.extend;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.smallbox.faraway.LuaModule;
import org.smallbox.faraway.LuaModuleManager;

/**
 * Created by Alex on 29/09/2015.
 */
public interface LuaExtendInterface {
    boolean accept(String type);
    void extend(LuaModuleManager luaModuleManager, LuaModule module, Globals globals, LuaValue value);
}
