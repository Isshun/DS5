package org.smallbox.faraway.client.lua;

import com.steadystate.css.dom.CSSValueImpl;
import com.steadystate.css.dom.RGBColorImpl;
import org.smallbox.faraway.client.ui.engine.views.widgets.View;
import org.smallbox.faraway.common.ColorUtils;
import org.w3c.dom.css.CSSStyleDeclaration;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alex on 11/07/2016.
 */
public class LuaStyleManager {
    private static LuaStyleManager _self;
    private Map<String, CSSStyleDeclaration> _rules = new HashMap<>();

    public static LuaStyleManager getInstance() {
        if (_self == null) {
            _self = new LuaStyleManager();
        }
        return _self;
    }

    public void applyStyleFromId(String viewId, View view) {
        if (_rules.containsKey("#" + viewId)) {
            CSSStyleDeclaration style = _rules.get("#" + viewId);
            if (style.getPropertyValue("background") != null) {
                int r = Integer.valueOf(((RGBColorImpl)((CSSValueImpl)style.getPropertyCSSValue("background")).getValue()).getRed().toString());
                int g = Integer.valueOf(((RGBColorImpl)((CSSValueImpl)style.getPropertyCSSValue("background")).getValue()).getGreen().toString());
                int b = Integer.valueOf(((RGBColorImpl)((CSSValueImpl)style.getPropertyCSSValue("background")).getValue()).getBlue().toString());
                view.setBackgroundColor(ColorUtils.fromHex(r, g, b));
            }
        }
    }

    public void addRule(String selectorText, CSSStyleDeclaration rule) {
        _rules.put(selectorText, rule);
    }
}
