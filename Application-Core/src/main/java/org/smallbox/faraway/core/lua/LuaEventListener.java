package org.smallbox.faraway.core.lua;

import org.luaj.vm2.LuaValue;

public interface LuaEventListener {
    boolean onEvent(int eventId, LuaValue tag, LuaValue luaData);
}
