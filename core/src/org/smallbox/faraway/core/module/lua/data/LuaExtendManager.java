package org.smallbox.faraway.core.module.lua.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 29/09/2015.
 */
public class LuaExtendManager {
    private List<LuaExtend> _luaExtends;

    public LuaExtendManager() {
        _luaExtends = new ArrayList<>();
    }

    public void addExtendFactory(LuaExtend luaExtend) {
        _luaExtends.add(luaExtend);
    }

    public List<LuaExtend> getExtends() {
        return _luaExtends;
    }
}
