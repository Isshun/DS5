package org.smallbox.faraway.client.lua.extend.impl;

import com.badlogic.gdx.graphics.Color;
import org.luaj.vm2.LuaValue;
import org.smallbox.faraway.client.asset.font.FontManager;
import org.smallbox.faraway.client.lua.extend.LuaUICompositeExtend;
import org.smallbox.faraway.client.ui.event.UIEventManager;
import org.smallbox.faraway.client.ui.widgets.UISlider;
import org.smallbox.faraway.client.ui.widgets.View;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.module.ModuleBase;

@ApplicationObject
public class LuaUISliderExtend extends LuaUICompositeExtend {
    @Inject private FontManager fontManager;

    @Override
    public boolean accept(String type) {
        return "slider".equals(type);
    }

    @Override
    protected void readSpecific(LuaValue style, LuaValue value, View view) {
        UISlider slider = (UISlider) view;

        readLua(style, value, "handle_size", v -> {slider.handleWidth = v.get(1).toint(); slider.handleHeight = v.get(2).toint();});
        readInt(style, value, "handle_background", v -> slider.handleBackground = new Color(v));

        slider.getEvents().setOnDragListener(new UIEventManager.OnDragListener() {
            @Override
            public void onDrag(int x, int y) {
            }

            @Override
            public void onDragMove(int x, int y) {
                slider.update(x);
            }

            @Override
            public void onDrop(int x, int y, View dropView) {

            }

            @Override
            public void onHover(int x, int y, View dropView) {

            }

            @Override
            public void onHoverExit(int x, int y, View dropView) {

            }
        });
    }

    @Override
    protected View createViewFromType(ModuleBase module, LuaValue value) {
        return new UISlider(module);
    }

}