package org.smallbox.faraway.client.layer;

import org.smallbox.faraway.client.LayerLevel;
import org.smallbox.faraway.client.renderer.BaseRenderer;
import org.smallbox.faraway.client.renderer.Viewport;
import org.smallbox.faraway.client.ui.TransitionManager;
import org.smallbox.faraway.client.ui.UIManager;
import org.smallbox.faraway.client.ui.widgets.View;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;

@GameObject
@GameLayer(level = LayerLevel.UI, visible = true)
public class UILayer extends BaseLayer {
    @Inject private UIManager uiManager;
    @Inject private TransitionManager transitionManager;

    public View _debugView;

    @Override
    protected void onDraw(BaseRenderer renderer, Viewport viewport, double animProgress, int frame) {
        uiManager.draw(renderer, true);
        transitionManager.draw();

        if (_debugView != null) {
            _debugView.draw(renderer, 0, 0);
        }
    }

}
