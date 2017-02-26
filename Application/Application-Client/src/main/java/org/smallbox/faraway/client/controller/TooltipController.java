package org.smallbox.faraway.client.controller;

import org.smallbox.faraway.client.renderer.Viewport;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.client.ui.engine.views.widgets.View;
import org.smallbox.faraway.core.dependencyInjector.BindComponent;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
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

    @BindLua
    private View subView;

    @BindModule
    private WorldModule worldModule;

    @BindModule
    private CharacterModule characterModule;

    @BindComponent
    private Viewport viewport;

    private Map<String, View> subViews = new ConcurrentHashMap<>();

    public void addSubView(String name, View view) {
        subViews.forEach((s, view1) -> view1.setVisible(false));
        subViews.put(name, view);
        view.setVisible(true);

        StringBuilder sb = new StringBuilder();
        subViews.entrySet().forEach(entry -> sb.append(entry.getKey() != null ? entry.getKey() : ""));
        lbName.setText(sb.toString());
    }

    public void removeSubView(String name) {
        View view = subViews.get(name);
        if (view != null) {
            view.setVisible(false);
        }
    }

    @Override
    protected void onNewGameUpdate(Game game) {

    }
}
