package org.smallbox.faraway.core.engine.module.lua.data.extend;

import com.badlogic.gdx.Input;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.core.engine.module.ModuleBase;
import org.smallbox.faraway.core.engine.module.lua.data.DataExtendException;
import org.smallbox.faraway.core.engine.module.lua.data.LuaExtend;
import org.smallbox.faraway.core.game.DataManager;
import org.smallbox.faraway.core.game.modelInfo.BindingInfo;

import java.io.File;

@ApplicationObject
public class LuaBindingsExtend extends LuaExtend {

    @Override
    public boolean accept(String type) {
        return "binding".equals(type);
    }

    @Override
    public void extend(DataManager dataManager, ModuleBase module, Globals globals, LuaValue value, File dataDirectory) throws DataExtendException {
        try {
            // Read bindings
            BindingInfo bindingInfo = new BindingInfo();

            if (!value.get("key").isnil()) {
                bindingInfo.key = Input.Keys.valueOf(getString(value, "key", null));
            }

            if (!value.get("modifier").isnil()) {
                bindingInfo.modifier = GameEventListener.Modifier.valueOf(getString(value, "modifier", null));
            }

            bindingInfo.label = getString(value, "label", null);
            bindingInfo.name = getString(value, "id", null);
            bindingInfo.check = () -> {
                LuaValue ret = value.get("on_check").call();
                return !ret.isnil() && ret.toboolean();
            };
            bindingInfo.action = () -> value.get("on_action").call();

            dataManager.bindings.add(bindingInfo);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}
