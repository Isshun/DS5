package org.smallbox.faraway.client.layer;

import org.smallbox.faraway.client.renderer.BaseRenderer;
import org.smallbox.faraway.client.renderer.Viewport;
import org.smallbox.faraway.client.ui.UIManager;
import org.smallbox.faraway.client.ui.widgets.View;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;

@GameObject
@GameLayer(level = 1, visible = true)
public class UILayer extends BaseLayer {
    @Inject private UIManager uiManager;

    public View _debugView;

    @Override
    protected void onDraw(BaseRenderer renderer, Viewport viewport, double animProgress, int frame) {
        uiManager.draw(renderer, true);

        if (_debugView != null) {
            _debugView.draw(renderer, 0, 0);
        }
    }

}
