package org.smallbox.faraway.client.controller;

import org.smallbox.faraway.client.renderer.Viewport;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.client.ui.engine.views.widgets.View;
import org.smallbox.faraway.core.GameShortcut;
import org.smallbox.faraway.core.dependencyInjector.BindComponent;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.lua.BindLua;
import org.smallbox.faraway.modules.character.CharacterModule;
import org.smallbox.faraway.modules.world.WorldModule;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Alex on 26/04/2016.
 */
public class TooltipController extends LuaController {

    @BindLua
    private UILabel lbName;

    @BindModule
    private WorldModule worldModule;

    @BindModule
    private CharacterModule characterModule;

    @BindComponent
    private Viewport viewport;

    private Map<String, View> subViews = new ConcurrentHashMap<>();

    public void addSubView(String name, View view) {
        subViews.put(name, view);

        refreshLabels();
    }

    public void removeSubView(String name) {
        subViews.remove(name);

        for (View subView: subViews.values()) {
            subView.setVisible(true);
            break;
        }

        refreshLabels();
    }

    private void refreshLabels() {
        StringBuilder sb = new StringBuilder();
        subViews.entrySet().forEach(entry -> {
            if (entry.getValue().isVisible()) {
                sb.append("[").append(entry.getKey().toUpperCase()).append("] ");
            } else {
                sb.append(" ").append(entry.getKey()).append("  ");
            }
        });
        lbName.setText(sb.toString());
    }

    @GameShortcut(key = GameEventListener.Key.TAB)
    public void onNextView() {
        boolean selectNext = false;
        for (View view: subViews.values()) {
            if (selectNext) {
                view.setVisible(true);
                refreshLabels();
                return;
            }
            if (view.isVisible()) {
                selectNext = true;
            }
        }
        for (View view: subViews.values()) {
            view.setVisible(true);
            refreshLabels();
            return;
        }
    }

    @Override
    protected void onNewGameUpdate(Game game) {

    }
}