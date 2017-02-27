package org.smallbox.faraway.client.debug.renderer;

import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.client.renderer.BaseRenderer;
import org.smallbox.faraway.client.renderer.GDXRenderer;
import org.smallbox.faraway.client.renderer.Viewport;
import org.smallbox.faraway.client.ui.UIManager;
import org.smallbox.faraway.client.ui.engine.UIEventManager;
import org.smallbox.faraway.client.ui.engine.views.widgets.View;
import org.smallbox.faraway.core.GameRenderer;
import org.smallbox.faraway.core.GameShortcut;
import org.smallbox.faraway.core.dependencyInjector.BindComponent;
import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.util.CollectionUtils;

/**
 * Created by Alex on 31/07/2016.
 */
@GameRenderer(level = 2, visible = false)
public class DebugViewRenderer extends BaseRenderer {

    @BindComponent
    private UIManager uiManager;

    @BindComponent
    private UIEventManager uiEventManager;

    public void    onDraw(GDXRenderer renderer, Viewport viewport, double animProgress) {
        uiManager.getRootViews().forEach(rootView -> drawViewRecurse(renderer, rootView.getView()));

//        uiManager.getViews().stream()
//                .sorted(View::compareLevel)
//                .filter(View::isVisible)
//                .filter(View::isLeaf)
//                .forEach(view -> {
//                    renderer.drawRectangle(view.getFinalX(), view.getFinalY(), view.getWidth(), view.getHeight(), uiEventManager.hasClickListener(view) ? Color.YELLOW : Color.RED, false);
//                    renderer.drawText(view.getFinalX(), view.getFinalY(), 12, Color.RED, view.getName());
//                    renderer.drawText(view.getFinalX(), view.getFinalY() + 10, 12, Color.RED, view.getPath());
//                    renderer.drawText(view.getFinalX(), view.getFinalY() + 20, 12, Color.RED, "size: " + view.getViews().size());
//                });
    }

    private void drawViewRecurse(GDXRenderer renderer, View view) {
        if (view.isVisible()) {
            renderer.drawRectangle(view.getFinalX(), view.getFinalY(), view.getWidth(), view.getHeight(), uiEventManager.hasClickListener(view) ? Color.YELLOW : Color.RED, false);
            renderer.drawText(view.getFinalX(), view.getFinalY(), 12, Color.RED, view.getName());
            renderer.drawText(view.getFinalX(), view.getFinalY() + 10, 12, Color.RED, view.getPath());
            renderer.drawText(view.getFinalX(), view.getFinalY() + 20, 12, Color.RED, "size: " + view.getViews().size());

            if (CollectionUtils.isNotEmpty(view.getViews())) {
                view.getViews().forEach(v -> drawViewRecurse(renderer, v));
            }
        }
    }

    @SuppressWarnings("unused")
    @GameShortcut(key = GameEventListener.Key.F6)
    public void onToggleVisibility() {
        toggleVisibility();
    }

}
