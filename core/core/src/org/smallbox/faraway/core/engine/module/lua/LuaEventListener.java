package org.smallbox.faraway.core.engine.module.lua;

import org.luaj.vm2.LuaValue;

/**
 * Created by Alex on 26/09/2015.
 */
public interface LuaEventListener {
    boolean onEvent(int eventId, LuaValue tag, LuaValue luaData);
}
