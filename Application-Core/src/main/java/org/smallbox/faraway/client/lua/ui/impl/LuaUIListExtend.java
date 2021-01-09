package org.smallbox.faraway.client.lua.ui.impl;

import org.luaj.vm2.LuaValue;
import org.smallbox.faraway.client.lua.ui.LuaUICompositeExtend;
import org.smallbox.faraway.client.ui.engine.views.View;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIList;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.engine.module.ModuleBase;

@ApplicationObject
public class LuaUIListExtend extends LuaUICompositeExtend {

    @Override
    public boolean accept(String type) {
        return "list".equals(type);
    }

    @Override
    protected void readSpecific(LuaValue value, View view) {
    }

    @Override
    protected View createViewFromType(ModuleBase module, LuaValue value) {
        return new UIList(module);
    }

}