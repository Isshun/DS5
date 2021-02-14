package org.smallbox.faraway.client.lua.extend.impl;

import org.luaj.vm2.LuaValue;
import org.smallbox.faraway.client.lua.extend.LuaUICompositeExtend;
import org.smallbox.faraway.client.ui.widgets.UIFrame;
import org.smallbox.faraway.client.ui.widgets.View;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.module.ModuleBase;

@ApplicationObject
public class LuaUIFrameExtend extends LuaUICompositeExtend {

    @Override
    public boolean accept(String type) {
        return "view".equals(type);
    }

    @Override
    protected void readSpecific(LuaValue style, LuaValue value, View view) {
    }

    @Override
    protected View createViewFromType(ModuleBase module, LuaValue value) {
        return new UIFrame(module);
    }

}