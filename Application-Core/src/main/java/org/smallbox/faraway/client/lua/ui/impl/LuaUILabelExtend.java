package org.smallbox.faraway.client.lua.ui.impl;

import org.luaj.vm2.LuaValue;
import org.smallbox.faraway.client.lua.ui.LuaUIExtend;
import org.smallbox.faraway.client.ui.engine.views.View;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.engine.module.ModuleBase;

@ApplicationObject
public class LuaUILabelExtend extends LuaUIExtend {

    @Override
    public boolean accept(String type) {
        return "label".equals(type);
    }

    @Override
    protected void readSpecific(LuaValue value, View view) {
        UILabel label = (UILabel) view;
        readString(value, "text", label::setText);
        readInt(value, "text_size", label::setTextSize);
        readInt(value, "text_color", label::setTextColor);
        // TODO
        //readString(value, "text_align", label::setTextAlign);
    }

    @Override
    protected View createViewFromType(ModuleBase module, LuaValue value) {
        return new UILabel(module);
    }

}