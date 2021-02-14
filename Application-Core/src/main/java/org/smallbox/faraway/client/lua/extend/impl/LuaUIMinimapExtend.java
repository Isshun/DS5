package org.smallbox.faraway.client.lua.extend.impl;

import org.luaj.vm2.LuaValue;
import org.smallbox.faraway.client.layer.ui.MinimapLayer;
import org.smallbox.faraway.client.lua.extend.LuaUIExtend;
import org.smallbox.faraway.client.renderer.BaseRenderer;
import org.smallbox.faraway.client.ui.widgets.View;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.module.ModuleBase;

@ApplicationObject
public class LuaUIMinimapExtend extends LuaUIExtend {
    @Inject private MinimapLayer minimapLayer;

    @Override
    public boolean accept(String type) {
        return "minimap".equals(type);
    }

    @Override
    protected void readSpecific(LuaValue style, LuaValue value, View view) {
    }

    @Override
    protected View createViewFromType(ModuleBase module, LuaValue value) {
        return new View(module) {
            @Override
            public void draw(BaseRenderer renderer, int x, int y) {
                if (_isVisible && minimapLayer != null && minimapLayer.getSprite() != null) {
                    geometry.setFinalX(getAlignedX() + geometry.getMarginLeft() + x);
                    geometry.setFinalY(geometry.getY() + geometry.getMarginTop() + y);
                    renderer.drawSprite(minimapLayer.getSprite());
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