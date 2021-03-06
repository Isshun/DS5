package org.smallbox.faraway.module.dev;

import org.smallbox.faraway.GameEvent;
import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.client.ui.widgets.UILabel;
import org.smallbox.faraway.client.ui.widgets.View;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.core.lua.BindLua;
import org.smallbox.faraway.debug.DebugModule;

import java.util.ArrayList;
import java.util.List;

public class ClickDebugController extends LuaController {
    @BindModule
    private DebugModule module;

    @BindLua
    private UILabel entry;

    private String  current = "";

    @Override
    public void onMouseMove(GameEvent event) {

//        ApplicationClient.uiEventManager.getClickListeners().keySet().forEach(view -> {
//            if (view.isActive() && ApplicationClient.uiEventManager.hasVisibleHierarchy(view)) {
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

    @Override
    public boolean onKeyPress(GameEventListener.Key key) {
        System.out.println("Console: " + key);

        switch (key) {
            case ENTER:
                module.execute(current);
                current = "";
                entry.setText(" ");
                break;
            case BACKSPACE:
                current = current.length() > 0 ? current.substring(0, current.length() - 1) : "";
                entry.setText(current);
                break;
            default:
                current += key.getText();
                entry.setText(current);
                break;
        }

        return false;
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
