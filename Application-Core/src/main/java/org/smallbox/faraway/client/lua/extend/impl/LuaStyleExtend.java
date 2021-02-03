package org.smallbox.faraway.client.lua.extend.impl;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.smallbox.faraway.client.ui.UIManager;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.DataManager;
import org.smallbox.faraway.core.lua.data.LuaExtend;
import org.smallbox.faraway.core.module.ModuleBase;

import java.io.File;

@ApplicationObject
public class LuaStyleExtend extends LuaExtend {
    @Inject private UIManager uiManager;

    @Override
    public boolean accept(String type) {
        return "style".equals(type);
    }

    @Override
    public void extend(DataManager dataManager, ModuleBase module, Globals globals, LuaValue value, File dataDirectory) {
        readTable(value, "styles", (subValue, index) -> uiManager.addStyle(getString(subValue, "id", null), subValue));
    }

}