package org.smallbox.faraway.client.debug.layer;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import org.apache.commons.collections4.CollectionUtils;
import org.smallbox.faraway.client.render.GDXRendererBase;
import org.smallbox.faraway.client.render.Viewport;
import org.smallbox.faraway.client.render.layer.BaseLayer;
import org.smallbox.faraway.client.ui.UIManager;
import org.smallbox.faraway.client.ui.engine.UIEventManager;
import org.smallbox.faraway.client.ui.engine.views.CompositeView;
import org.smallbox.faraway.client.ui.engine.views.View;
import org.smallbox.faraway.core.GameLayer;
import org.smallbox.faraway.core.GameShortcut;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;

@GameObject
@GameLayer(level = 999, visible = false)
public class DebugViewLayer extends BaseLayer {
    @Inject private UIManager uiManager;
    @Inject private UIEventManager uiEventManager;

    public void    onDraw(GDXRendererBase renderer, Viewport viewport, double animProgress, int frame) {
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

    private void drawViewRecurse(GDXRendererBase renderer, View view) {
        if (view.isVisible()) {
            if (uiEventManager.hasClickListener(view)) {
                renderer.drawRectangle(view.getGeometry().getFinalX(), view.getGeometry().getFinalY(), view.getWidth(), view.getHeight(), Color.YELLOW, false);
                renderer.drawText(view.getGeometry().getFinalX() + 3, view.getGeometry().getFinalY() + 3, 12, Color.BLACK, view.getPath());
                renderer.drawText(view.getGeometry().getFinalX() + 2, view.getGeometry().getFinalY() + 2, 12, Color.YELLOW, view.getPath());
            } else {
                renderer.drawRectangle(view.getGeometry().getFinalX(), view.getGeometry().getFinalY(), view.getWidth(), view.getHeight(), uiEventManager.hasClickListener(view) ? Color.YELLOW : Color.SALMON, false);
                renderer.drawText(view.getGeometry().getFinalX() + 3, view.getGeometry().getFinalY() + 3, 12, Color.BLACK, view.getPath());
                renderer.drawText(view.getGeometry().getFinalX() + 2, view.getGeometry().getFinalY() + 2, 12, Color.RED, view.getPath());
            }
//            renderer.drawText(view.getFinalX(), view.getFinalY() + 10, 12, Color.RED, view.getPath());
//            renderer.drawText(view.getFinalX(), view.getFinalY() + 20, 12, Color.RED, "size: " + view.getViews().size());

            CompositeView.instanceOf(view).ifPresent(compositeView -> {
                if (CollectionUtils.isNotEmpty(compositeView.getViews())) {
                    compositeView.getViews().forEach(v -> drawViewRecurse(renderer, v));
                }
            });
        }
    }

    @GameShortcut(key = Input.Keys.F6)
    public void onToggleVisibility() {
        toggleVisibility();
    }

//    @GameShortcut(key = Input.Keys.F2)
//    public void hideAllViews() {
//        uiManager.getRootViews().forEach(rootView -> rootView.setVisible(false));
//    }

}
