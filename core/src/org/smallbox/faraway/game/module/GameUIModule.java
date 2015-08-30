package org.smallbox.faraway.game.module;

import org.smallbox.faraway.core.renderer.GDXRenderer;
import org.smallbox.faraway.ui.engine.LayoutFactory;
import org.smallbox.faraway.ui.engine.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 30/08/2015.
 */
public abstract class GameUIModule extends GameModule {
    private List<View> _panels = new ArrayList<>();

    public void draw(GDXRenderer renderer) {
        _panels.forEach(view -> view.draw(renderer, 0, 0));
    }

    protected void createPanel(String layoutPath, LayoutFactory.OnLayoutLoaded onLayoutLoaded) {
        LayoutFactory.load("data/ui/" + layoutPath, (layout, panel) -> {
            _panels.add(panel);
            onLayoutLoaded.onLayoutLoaded(layout, panel);
        });
    }


    @Override
    protected void onDestroy() {
        _panels.clear();
    }

}
