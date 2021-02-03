package org.smallbox.faraway.client.lua.extend.impl;

import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import org.luaj.vm2.LuaValue;
import org.smallbox.faraway.client.asset.font.FontManager;
import org.smallbox.faraway.client.lua.extend.LuaUIExtend;
import org.smallbox.faraway.client.ui.event.DefaultFocusListener;
import org.smallbox.faraway.client.ui.widgets.UILabel;
import org.smallbox.faraway.client.ui.widgets.View;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.module.ModuleBase;

@ApplicationObject
public class LuaUILabelExtend extends LuaUIExtend {
    @Inject private FontManager fontManager;

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
        readInt(style, value, "text_focus_color", focusColor -> {
            label.setTextFocusColor(focusColor);
            label.getEvents().setOnFocusListener(new DefaultFocusListener());
        });
        readString(style, value, "text", label::setText);

        readLua(style, value, "size", v -> view.setSize(v.get(1).toint(), v.get(2).toint()), v -> {
            GlyphLayout glyphLayout = new GlyphLayout();
            glyphLayout.setText(fontManager.getFont(label.getFont(), label.getTextSize()), "text");
            view.setSize(View.FILL, (int) glyphLayout.height);
        });
        readLua(style, value, "size", v -> view.getGeometry().setFixedSize(v.get(1).toint(), v.get(2).toint()));
    }

    @Override
    protected View createViewFromType(ModuleBase module, LuaValue value) {
        return new UILabel(module);
    }

}