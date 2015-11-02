package org.smallbox.faraway.core.module.lua;

import org.luaj.vm2.LuaValue;

/**
 * Created by Alex on 26/09/2015.
 */
public interface LuaEventListener {
    void onEvent(int eventId, LuaValue tag, LuaValue luaData);
}
