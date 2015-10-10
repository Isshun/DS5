package org.smallbox.faraway.module.lua.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 29/09/2015.
 */
public class LuaExtendManager {
    private List<LuaExtendInterface> _luaExtends;

    public LuaExtendManager() {
        _luaExtends = new ArrayList<>();
    }

    public void addExtendFactory(LuaExtendInterface luaExtend) {
        _luaExtends.add(luaExtend);
    }

    public List<LuaExtendInterface> getExtends() {
        return _luaExtends;
    }
}
