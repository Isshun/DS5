package org.smallbox.faraway.client.lua.extend.impl;

import org.luaj.vm2.LuaValue;
import org.smallbox.faraway.client.lua.extend.LuaUIExtend;
import org.smallbox.faraway.client.render.GDXRenderer;
import org.smallbox.faraway.client.ui.engine.views.View;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.engine.module.ModuleBase;

@ApplicationObject
public class LuaUIMinimapExtend extends LuaUIExtend {

    @Override
    public boolean accept(String type) {
        return "minimap".equals(type);
    }

    @Override
    protected void readSpecific(LuaValue value, View view) {
    }

    @Override
    protected View createViewFromType(ModuleBase module, LuaValue value) {
        return new View(module) {
            @Override
            public void draw(GDXRenderer renderer, int x, int y) {
                if (_isVisible) {
                    geometry.setFinalX(getAlignedX() + geometry.getMarginLeft() + x);
                    geometry.setFinalY(geometry.getY() + geometry.getMarginTop() + y);
                }
            }

            @Override
            public int getContentWidth() {
                return 0;
            }

            @Override
            public int getContentHeight() {
                return 0;
            }
        };
    }

}