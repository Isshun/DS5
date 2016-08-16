package org.smallbox.faraway.module.dev;

import org.smallbox.faraway.GameEvent;
import org.smallbox.faraway.core.game.BindLua;
import org.smallbox.faraway.core.game.module.character.controller.LuaController;
import org.smallbox.faraway.ui.engine.views.widgets.UIList;
import org.smallbox.faraway.ui.engine.views.widgets.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 30/08/2015.
 */
public class ClickDebugController extends LuaController {
    @BindLua
    private UIList viewsList;

    @Override
    public void onMouseMove(GameEvent event) {
        viewsList.clear();

//        UIEventManager.getInstance().getClickListeners().keySet().forEach(view -> {
//            if (view.isActive() && UIEventManager.getInstance().hasVisibleHierarchy(view)) {
//                if (view.contains(event.x, event.y)) {
//                    String name = getAbsoluteName(view);
//
//                    UILabel label = new UILabel(null);
//                    label.setText((view.isVisible() ? "[*] " : "[ ] ") + "(name: " + name + ", action: " + view.getActionName() + ")");
//                    label.setSize(100, 13);
//                    label.setTextSize(12);
//                    viewsList.addView(label);
//                }
//            }
//        });
    }

    private String getAbsoluteName(View view) {
        List<String> names = new ArrayList<>();
        while (view != null) {
            names.add(0, view.getName());
            view = view.getParent();
        }
        return String.join(".", names);
    }
}
