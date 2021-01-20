package org.smallbox.faraway.client.lua.extend.impl;

import org.luaj.vm2.LuaValue;
import org.smallbox.faraway.client.lua.extend.LuaUIExtend;
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
        readString(value, "text_font", label::setFont);
        readBoolean(value, "text_outlined", label::setOutlined);
        readString(value, "text_align", label::setTextAlign);
        readInt(value, "text_length", label::setTextLength);
        readInt(value, "text_size", label::setTextSize);
        readInt(value, "text_color", label::setTextColor);
        readString(value, "text", label::setText);
    }

    @Override
    protected View createViewFromType(ModuleBase module, LuaValue value) {
        return new UILabel(module);
    }

}