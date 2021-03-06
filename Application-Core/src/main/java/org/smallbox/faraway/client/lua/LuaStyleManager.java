package org.smallbox.faraway.client.lua;

import com.badlogic.gdx.graphics.Color;
import com.steadystate.css.dom.CSSValueImpl;
import com.steadystate.css.dom.RGBColorImpl;
import org.smallbox.faraway.client.ui.widgets.View;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.w3c.dom.css.CSSStyleDeclaration;

import java.util.HashMap;
import java.util.Map;

@ApplicationObject
public class LuaStyleManager {
    private final Map<String, CSSStyleDeclaration> _rules = new HashMap<>();

    public void applyStyleFromId(String viewId, View view) {
        if (_rules.containsKey("#" + viewId)) {
            CSSStyleDeclaration style = _rules.get("#" + viewId);
            if (style.getPropertyValue("background") != null) {
                int r = Integer.parseInt(((RGBColorImpl)((CSSValueImpl)style.getPropertyCSSValue("background")).getValue()).getRed().toString());
                int g = Integer.parseInt(((RGBColorImpl)((CSSValueImpl)style.getPropertyCSSValue("background")).getValue()).getGreen().toString());
                int b = Integer.parseInt(((RGBColorImpl)((CSSValueImpl)style.getPropertyCSSValue("background")).getValue()).getBlue().toString());
                view.getStyle().setBackgroundColor(new Color(r / 255f, g / 255f, b / 255f, 1f));
            }
        }
    }

    public void addRule(String selectorText, CSSStyleDeclaration rule) {
        _rules.put(selectorText, rule);
    }
}
