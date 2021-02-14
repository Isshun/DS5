package org.smallbox.faraway.client.lua.extend.impl;

import org.luaj.vm2.LuaValue;
import org.smallbox.faraway.client.lua.extend.LuaUICompositeExtend;
import org.smallbox.faraway.client.ui.widgets.UICheckBox;
import org.smallbox.faraway.client.ui.widgets.View;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.module.ModuleBase;

@ApplicationObject
public class LuaUICheckBoxExtend extends LuaUICompositeExtend {

    @Override
    public boolean accept(String type) {
        return "checkbox".equals(type);
    }

    @Override
    protected void readSpecific(LuaValue style, LuaValue value, View view) {
        UICheckBox checkBox = (UICheckBox) view;
        readString(value, "text", checkBox::setText);
        readInt(value, "text_size", checkBox::setTextSize);
        readInt(value, "text_color", checkBox::setTextColor);
    }

    @Override
    protected View createViewFromType(ModuleBase module, LuaValue value) {
        return new UICheckBox(module);
    }

}