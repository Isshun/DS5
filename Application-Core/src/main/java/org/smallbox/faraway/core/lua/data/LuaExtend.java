package org.smallbox.faraway.core.lua.data;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.smallbox.faraway.core.dependencyInjector.DependencyManager;
import org.smallbox.faraway.core.game.DataManager;
import org.smallbox.faraway.core.module.ModuleBase;

import java.io.File;

public abstract class LuaExtend {
    public abstract boolean accept(String type);
    public abstract void extend(DataManager dataManager, ModuleBase module, Globals globals, LuaValue value, File dataDirectory) throws DataExtendException;

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

    protected interface ReadTableCallback {
        void onReadCallback(LuaValue subValue, int index);
    }

    protected void readInt(LuaValue value, String key, ReadCallback<Integer> callback, int... def) {
        readInt(null, value, key, callback, def);
    }

    protected void readInt(LuaValue style, LuaValue value, String key, ReadCallback<Integer> callback, int... def) {
        if (style != null && !style.get(key).isnil()) {
            callback.onReadCallback(style.get(key).toint());
        }
        if (!value.get(key).isnil()) {
            callback.onReadCallback(value.get(key).toint());
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

    protected void readFloat(LuaValue value, String key, ReadCallback<Float> callback, float... def) {
        LuaValue v = value.get(key);
        if (!v.isnil()) {
            callback.onReadCallback(v.tofloat());
        } else if (def.length > 0) {
            callback.onReadCallback(def[0]);
        }
    }

    protected void readString(LuaValue value, String key, ReadCallback<String> callback) {
        readString(null, value, key, callback);
    }

    protected void readString(LuaValue style, LuaValue value, String key, ReadCallback<String> callback) {
        if (style != null && !style.get(key).isnil()) {
            callback.onReadCallback(style.get(key).tojstring());
        }
        if (!value.get(key).isnil()) {
            callback.onReadCallback(value.get(key).tojstring());
        }
    }

    protected void readTable(LuaValue value, String key, ReadTableCallback callback) {
        LuaValue subValues = value.get(key);
        if (!subValues.isnil()) {
            if (subValues.istable()) {
                for (int index = 1; index <= subValues.length(); index++) {
                    callback.onReadCallback(subValues.get(index), index);
                }
            } else {
                callback.onReadCallback(subValues.get(1), 1);
            }
        }
    }

    protected void readBoolean(LuaValue value, String key, ReadCallback<Boolean> callback, boolean... def) {
        readBoolean(null, value, key, callback, def);
    }

    protected void readBoolean(LuaValue style, LuaValue value, String key, ReadCallback<Boolean> callback, boolean... def) {
        if (style != null && !style.get(key).isnil()) {
            callback.onReadCallback(style.get(key).toboolean());
        }
        if (!value.get(key).isnil()) {
            callback.onReadCallback(value.get(key).toboolean());
        } else if (def.length > 0) {
            callback.onReadCallback(def[0]);
        }
    }

    protected void readLua(LuaValue value, String key, ReadCallback<LuaValue> callback) {
        readLua(null, value, key, callback);
    }

    protected void readLua(LuaValue style, LuaValue value, String key, ReadCallback<LuaValue> callback) {
        if (style != null && !style.get(key).isnil()) {
            callback.onReadCallback(style.get(key));
        }
        if (!value.get(key).isnil()) {
            callback.onReadCallback(value.get(key));
        }
    }

    protected void readLua(LuaValue value, String key, ReadCallback<LuaValue> callback, ReadCallback<LuaValue> callbackNotExist) {
        readLua(null, value, key, callback, callbackNotExist);
    }

    protected void readLua(LuaValue style, LuaValue value, String key, ReadCallback<LuaValue> callback, ReadCallback<LuaValue> callbackNotExist) {
        if (style != null && !style.get(key).isnil()) {
            callback.onReadCallback(style.get(key));
        }
        if (!value.get(key).isnil()) {
            callback.onReadCallback(value.get(key));
        }
        if ((style == null || style.get(key).isnil()) && value.get(key).isnil()) {
            callbackNotExist.onReadCallback(null);
        }
    }

    protected <T> void readAsync(LuaValue value, String key, Class<T> cls, ReadCallback<T> callback) {
        LuaValue v = value.get(key);
        if (!v.isnil()) {
            DependencyManager.getInstance().getDependency(DataManager.class).getAsync(v.tojstring(), cls, callback::onReadCallback);
        }
    }

}
