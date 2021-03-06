package org.smallbox.faraway.client.lua.extend;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.smallbox.faraway.client.ui.widgets.CompositeView;
import org.smallbox.faraway.client.ui.widgets.UIFrame;
import org.smallbox.faraway.client.ui.widgets.UIList;
import org.smallbox.faraway.client.ui.widgets.View;
import org.smallbox.faraway.core.module.ModuleBase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class LuaUICompositeExtend extends LuaUIExtend {

    public View createView(ModuleBase module, Globals globals, LuaValue value, boolean inGame, int deep, CompositeView parent, String path, int index, boolean isGameView, boolean runAfter, Map<String, LuaValue> styles) {
        View view = super.createView(module, globals, value, inGame, deep, parent, path, index, isGameView, runAfter, styles);

        readTemplate(module, globals, value, inGame, deep, view, path, isGameView, styles);

        return view;
    }

    /**
     * Read template from lua.
     * When template contains several views, they are encapsulated in UIList
     */
    private void readTemplate(ModuleBase module, Globals globals, LuaValue value, boolean inGame, int deep, View view, String path, boolean isGameView, Map<String, LuaValue> styles) {
        if (!value.get("template").isnil()) {
            ((CompositeView) view).setTemplate(() -> {
                List<View> templateViews = new ArrayList<>();
                readTable(value, "template", (subValue, i) -> templateViews.add(
                        clientLuaModuleManager.createView(module, globals, subValue, inGame, deep + 1, (CompositeView) view, path + "." + i, i, isGameView, false, styles)
                ));

                if (templateViews.size() == 1) {
                    UIFrame frame = new UIFrame(module);
                    frame.addView(templateViews.get(0));
                    frame.setSize(templateViews.get(0).getWidth(), templateViews.get(0).getHeight());
                    return frame;
                }

                if (templateViews.size() > 1) {
                    return new UIList(module, templateViews);
                }

                return null;
            });
        }
    }

}