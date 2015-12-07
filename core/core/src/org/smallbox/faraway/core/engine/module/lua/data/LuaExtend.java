package org.smallbox.faraway.core.engine.module.lua.data;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.smallbox.faraway.core.engine.module.lua.LuaModule;

/**
 * Created by Alex on 29/09/2015.
 */
public abstract class LuaExtend {
    public abstract boolean accept(String type);
    public abstract void extend(LuaModule module, Globals globals, LuaValue value) throws DataExtendException;

    protected static boolean getBoolean(LuaValue value, String key, boolean defaultValue) {
        return !value.get(key).isnil() ? value.get(key).toboolean() : defaultValue;
    }

    protected static double getDouble(LuaValue value, String key, double defaultValue) {
        return !value.get(key).isnil() ? value.get(key).todouble() : defaultValue;
    }

    protected static int getInt(LuaValue value, String key, int defaultValue) {
        return !value.get(key).isnil() ? value.get(key).toint() : defaultValue;
    }

    protected static int[] getIntInterval(LuaValue value, String key, int[] defaultValue) {
        if (!value.get(key).isnil() && value.get(key).length() == 2) {
            return new int[] {value.get(key).get(1).toint(), value.get(key).get(2).toint()};
        }
        return defaultValue;
    }

    protected static double[] getDoubleInterval(LuaValue value, String key, double[] defaultValue) {
        if (!value.get(key).isnil() && value.get(key).length() == 2) {
            return new double[] {value.get(key).get(1).todouble(), value.get(key).get(2).todouble()};
        }
        return defaultValue;
    }

    protected static String getString(LuaValue value, String key, String defaultValue) {
        return !value.get(key).isnil() ? value.get(key).toString() : defaultValue;
    }

}
