package org.smallbox.faraway.core.engine.module.lua.data.extend;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.smallbox.faraway.core.data.BindingInfo;
import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.core.game.model.Data;
import org.smallbox.faraway.core.engine.module.lua.DataExtendException;
import org.smallbox.faraway.core.engine.module.lua.LuaModule;
import org.smallbox.faraway.core.engine.module.lua.LuaModuleManager;
import org.smallbox.faraway.core.engine.module.lua.data.LuaExtend;

/**
 * Created by Alex on 04/11/2015.
 */
public class LuaBindingsExtend extends LuaExtend {
    @Override
    public boolean accept(String type) {
        return "binding".equals(type);
    }

    @Override
    public void extend(LuaModuleManager luaModuleManager, LuaModule module, Globals globals, LuaValue value) throws DataExtendException {
        try {
            // Read bindings
            BindingInfo bindingInfo = new BindingInfo();

            if (!value.get("key").isnil()) {
                bindingInfo.key = GameEventListener.Key.valueOf(getString(value, "key", null));
            }

            if (!value.get("modifier").isnil()) {
                bindingInfo.modifier = GameEventListener.Modifier.valueOf(getString(value, "modifier", null));
            }

            bindingInfo.label = getString(value, "label", null);
            bindingInfo.command = getString(value, "command", null);
            Data.getData().bindings.add(bindingInfo);

            // Put binding to LuaApplicationModel
            luaModuleManager.getGame().bindings.set(bindingInfo.command, CoerceJavaToLua.coerce(bindingInfo));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}
