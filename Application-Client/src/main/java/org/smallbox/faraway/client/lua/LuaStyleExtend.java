package org.smallbox.faraway.client.lua;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.smallbox.faraway.client.ApplicationClient;
import org.smallbox.faraway.core.engine.module.ModuleBase;
import org.smallbox.faraway.core.engine.module.lua.data.LuaExtend;

import java.io.File;

/**
 * Created by Alex on 29/09/2015.
 */
public class LuaStyleExtend extends LuaExtend {

    @Override
    public boolean accept(String type) {
        switch (type) {
            case "style":
                return true;
        }
        return false;
    }

    @Override
    public void extend(ModuleBase module, Globals globals, LuaValue value, File dataDirectory) {
        ApplicationClient.uiManager.addStyle(getString(value, "id", null), value.get("style"));
    }

}