package org.smallbox.faraway.client.lua.extend.impl;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.smallbox.faraway.client.ui.UIManager;
import org.smallbox.faraway.core.dependencyInjector.DependencyManager;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.engine.module.ModuleBase;
import org.smallbox.faraway.core.engine.module.lua.data.LuaExtend;
import org.smallbox.faraway.core.game.Data;

import java.io.File;

@ApplicationObject
public class LuaStyleExtend extends LuaExtend {

    @Override
    public boolean accept(String type) {
        if ("style".equals(type)) {
            return true;
        }
        return false;
    }

    @Override
    public void extend(Data data, ModuleBase module, Globals globals, LuaValue value, File dataDirectory) {
        DependencyManager.getInstance().getDependency(UIManager.class).addStyle(getString(value, "id", null), value.get("style"));
    }

}