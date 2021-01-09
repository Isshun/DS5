package org.smallbox.faraway.client.lua.extend;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.smallbox.faraway.client.ui.engine.views.CompositeView;
import org.smallbox.faraway.client.ui.engine.views.View;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIList;
import org.smallbox.faraway.core.engine.module.ModuleBase;

import java.util.ArrayList;
import java.util.List;

public abstract class LuaUICompositeExtend extends LuaUIExtend {

    public View createView(ModuleBase module, Globals globals, LuaValue value, boolean inGame, int deep, CompositeView parent, String path, int index, boolean isGameView, boolean runAfter) {
        View view = super.createView(module, globals, value, inGame, deep, parent, path, index, isGameView, runAfter);

        readTemplate(module, globals, value, inGame, deep, view, path, isGameView);

        return view;
    }

    /**
     * Read template from lua.
     * When template contains several views, they are encapsulated in UIList
     */
    private void readTemplate(ModuleBase module, Globals globals, LuaValue value, boolean inGame, int deep, View view, String path, boolean isGameView) {
        if (!value.get("template").isnil()) {
            ((CompositeView) view).setTemplate(() -> {
                List<View> templateViews = new ArrayList<>();
                readTable(value, "template", (subValue, i) -> templateViews.add(
                        clientLuaModuleManager.createView(module, globals, subValue, inGame, deep + 1, (CompositeView) view, path + "." + i, i, isGameView, false)
                ));

                if (templateViews.size() == 1) {
                    return templateViews.get(0);
                }

                if (templateViews.size() > 1) {
                    return new UIList(module, templateViews);
                }

                return null;
            });
        }
    }

}