package org.smallbox.faraway.core.engine.module.lua.data;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.smallbox.faraway.core.dependencyInjector.DependencyInjector;
import org.smallbox.faraway.core.engine.module.ModuleBase;
import org.smallbox.faraway.core.game.Data;

import java.io.File;

public abstract class LuaExtend {
    public abstract boolean accept(String type);
    public abstract void extend(Data data, ModuleBase module, Globals globals, LuaValue value, File dataDirectory) throws DataExtendException;

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
        if (!value.get(key).isnil() && value.get(key).istable() && value.get(key).length() == 2) {
            return new int[] {value.get(key).get(1).toint(), value.get(key).get(2).toint()};
        }
        if (!value.get(key).isnil() && value.get(key).isint()) {
            return new int[] {value.get(key).toint(), value.get(key).toint()};
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

    protected interface ReadCallback<T> {
        void onReadCallback(T value);
    }

    protected void readInt(LuaValue value, String key, ReadCallback<Integer> callback, int... def) {
        LuaValue v = value.get(key);
        if (!v.isnil()) {
            callback.onReadCallback(v.toint());
        } else if (def.length > 0) {
            callback.onReadCallback(def[0]);
        }
    }

    protected void readLong(LuaValue value, String key, ReadCallback<Long> callback, long... def) {
        LuaValue v = value.get(key);
        if (!v.isnil()) {
            callback.onReadCallback(v.tolong());
        } else if (def.length > 0) {
            callback.onReadCallback(def[0]);
        }
    }

    protected void readDouble(LuaValue value, String key, ReadCallback<Double> callback, double... def) {
        LuaValue v = value.get(key);
        if (!v.isnil()) {
            callback.onReadCallback(v.todouble());
        } else if (def.length > 0) {
            callback.onReadCallback(def[0]);
        }
    }

    protected void readString(LuaValue value, String key, ReadCallback<String> callback) {
        LuaValue v = value.get(key);
        if (!v.isnil()) {
            callback.onReadCallback(v.tojstring());
        }
    }

    protected void readBoolean(LuaValue value, String key, ReadCallback<Boolean> callback, boolean... def) {
        LuaValue v = value.get(key);
        if (!v.isnil()) {
            callback.onReadCallback(v.toboolean());
        } else if (def.length > 0) {
            callback.onReadCallback(def[0]);
        }
    }

    protected void readLua(LuaValue value, String key, ReadCallback<LuaValue> callback) {
        LuaValue v = value.get(key);
        if (!v.isnil()) {
            callback.onReadCallback(v);
        }
    }

    protected <T> void readAsync(LuaValue value, String key, Class<T> cls, ReadCallback<T> callback) {
        LuaValue v = value.get(key);
        if (!v.isnil()) {
            DependencyInjector.getInstance().getDependency(Data.class).getAsync(v.tojstring(), cls, callback::onReadCallback);
        }
    }

}
