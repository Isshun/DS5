package org.smallbox.faraway.client.lua.extend;

import com.badlogic.gdx.Input;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.smallbox.faraway.client.ApplicationClient;
import org.smallbox.faraway.common.DataAsyncListener;
import org.smallbox.faraway.common.GameEventListener;
import org.smallbox.faraway.common.ModuleBase;
import org.smallbox.faraway.common.lua.data.DataExtendException;
import org.smallbox.faraway.common.lua.data.LuaExtend;
import org.smallbox.faraway.common.modelInfo.BindingInfo;

import java.io.File;

/**
 * Created by Alex on 04/11/2015.
 */
public class LuaBindingsExtend extends LuaExtend {
    @Override
    public boolean accept(String type) {
        return "binding".equals(type);
    }

    @Override
    public void extend(ModuleBase module, Globals globals, LuaValue value, File dataDirectory) throws DataExtendException {
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
            bindingInfo.name = getString(value, "name", null);
            bindingInfo.check = () -> {
                LuaValue ret = value.get("on_check").call();
                return !ret.isnil() && ret.toboolean();
            };
            bindingInfo.action = () -> value.get("on_action").call();

            ApplicationClient.data.bindings.add(bindingInfo);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected <T> void getAsync(String itemName, Class<T> cls, DataAsyncListener<T> dataAsyncListener) {
        ApplicationClient.data.getAsync(itemName, cls, dataAsyncListener);
    }
}
