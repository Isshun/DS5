package org.smallbox.faraway.client.debug.dashboard;

import org.smallbox.faraway.client.debug.dashboard.content.ConsoleDashboardLayer;
import org.smallbox.faraway.client.renderer.BaseRenderer;
import org.smallbox.faraway.client.renderer.Viewport;
import org.smallbox.faraway.client.layer.BaseLayer;
import org.smallbox.faraway.core.GameLayer;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.OnInit;

@GameObject
@GameLayer(level = 999, visible = true)
public class MiniDashboardLayer extends BaseLayer {
    @Inject private ConsoleDashboardLayer consoleDashboardLayer;

    @OnInit
    public void init() {
    }

    @Override
    public void onDraw(BaseRenderer renderer, Viewport viewport, double animProgress, int frame) {
            consoleDashboardLayer.draw(renderer, frame);
    }

}