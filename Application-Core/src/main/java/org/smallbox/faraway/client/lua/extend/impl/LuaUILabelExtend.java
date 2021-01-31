package org.smallbox.faraway.client.lua.extend.impl;

import org.luaj.vm2.LuaValue;
import org.smallbox.faraway.client.lua.extend.LuaUIExtend;
import org.smallbox.faraway.client.ui.widgets.View;
import org.smallbox.faraway.client.ui.widgets.UILabel;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.module.ModuleBase;

@ApplicationObject
public class LuaUILabelExtend extends LuaUIExtend {

    @Override
    public boolean accept(String type) {
        return "label".equals(type);
    }

    @Override
    protected void readSpecific(LuaValue style, LuaValue value, View view) {
        UILabel label = (UILabel) view;
        readString(style, value, "text_font", label::setFont);
        readBoolean(style, value, "text_outlined", label::setOutlined);
        readInt(style, value, "shadow", label::setShadow);
        readInt(style, value, "shadow_color", label::setShadowColor);
        readString(style, value, "text_align", label::setTextAlign);
        readInt(style, value, "text_length", label::setTextLength);
        readInt(style, value, "text_size", label::setTextSize);
        readInt(style, value, "text_color", label::setTextColor);
        readString(style, value, "text", label::setText);
    }

    @Override
    protected View createViewFromType(ModuleBase module, LuaValue value) {
        return new UILabel(module);
    }

}