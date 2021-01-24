package org.smallbox.faraway.client.lua.extend.impl;

import org.luaj.vm2.LuaValue;
import org.smallbox.faraway.client.lua.extend.LuaUICompositeExtend;
import org.smallbox.faraway.client.ui.widgets.View;
import org.smallbox.faraway.client.ui.widgets.UIImage;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.module.ModuleBase;

@ApplicationObject
public class LuaUIImageExtend extends LuaUICompositeExtend {

    @Override
    public boolean accept(String type) {
        return "image".equals(type);
    }

    @Override
    protected void readSpecific(LuaValue value, View view) {
        UIImage imageView = (UIImage) view;
        readString(value, "src", imageView::setImage);
        readLua(value, "texture_rect", v -> imageView.setTextureRect(v.get(1).toint(), v.get(2).toint(), v.get(3).toint(), v.get(4).toint()));
    }

    @Override
    protected View createViewFromType(ModuleBase module, LuaValue value) {
        return new UIImage(module);
    }

}