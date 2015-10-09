package org.smallbox.faraway;

import org.luaj.vm2.LuaValue;

/**
 * Created by Alex on 26/09/2015.
 */
public interface LuaEventListener {
    void onEvent(int eventId, LuaValue luaData, LuaValue tag);
}
