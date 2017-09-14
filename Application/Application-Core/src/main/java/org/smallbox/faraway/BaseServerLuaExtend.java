package org.smallbox.faraway;

import org.smallbox.faraway.common.DataAsyncListener;
import org.smallbox.faraway.common.lua.data.LuaExtend;
import org.smallbox.faraway.core.Application;

public abstract class BaseServerLuaExtend extends LuaExtend {

    @Override
    protected <T> void getAsync(String itemName, Class<T> cls, DataAsyncListener<T> dataAsyncListener) {
        Application.data.getAsync(itemName, cls, dataAsyncListener);
    }

}
