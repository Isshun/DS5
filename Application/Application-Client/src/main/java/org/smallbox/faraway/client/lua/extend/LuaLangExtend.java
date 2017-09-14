package org.smallbox.faraway.client.lua.extend;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.smallbox.faraway.client.ApplicationClient;
import org.smallbox.faraway.common.DataAsyncListener;
import org.smallbox.faraway.common.ModuleBase;
import org.smallbox.faraway.common.lua.data.DataExtendException;
import org.smallbox.faraway.common.lua.data.LuaExtend;

import java.io.File;

/**
 * Created by Alex on 15/10/2015.
 */
public class LuaLangExtend extends LuaExtend {
    @Override
    public boolean accept(String type) {
        return "lang".equals(type);
    }

    @Override
    public void extend(ModuleBase module, Globals globals, LuaValue value, File dataDirectory) throws DataExtendException {
        LuaValue luaStrings = value.get("strings");
        if (!luaStrings.isnil()) {
            for (int i = 1; i <= luaStrings.length(); i++) {
                String str1 = luaStrings.get(i).get(1).toString();
                String str2 = luaStrings.get(i).get(2).toString();

                // Exact case
                ApplicationClient.data.strings.put(str1.hashCode(), str2);

                // Lower case
                if (!str1.toLowerCase().equals(str1)) {
                    ApplicationClient.data.strings.put(str1.toLowerCase().hashCode(), str2.toLowerCase());
                }

                // Capitalized
                ApplicationClient.data.strings.put(
                        (Character.toUpperCase(str1.toLowerCase().charAt(0)) + str1.substring(1)).hashCode(),
                        Character.toUpperCase(str2.toLowerCase().charAt(0)) + str2.substring(1));
            }
        }
    }

    @Override
    protected <T> void getAsync(String itemName, Class<T> cls, DataAsyncListener<T> dataAsyncListener) {
        ApplicationClient.data.getAsync(itemName, cls, dataAsyncListener);
    }
}
