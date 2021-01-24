package org.smallbox.faraway.client.debug.dashboard.content;

import org.smallbox.faraway.client.debug.dashboard.DashboardLayerBase;
import org.smallbox.faraway.client.renderer.BaseRenderer;
import org.smallbox.faraway.client.layer.LayerManager;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.Game;

@GameObject
public class LayerDashboardLayer extends DashboardLayerBase {
    @Inject private LayerManager layerManager;
    @Inject private Game game;

    @Override
    protected void onDraw(BaseRenderer renderer, int frame) {
        if (layerManager != null) {
            layerManager.getLayers().stream()
                    .sorted((o1, o2) -> (int)(o2.getCumulateTime() - o1.getCumulateTime()))
                    .forEach(render -> drawDebug(renderer, "Render",
                            String.format("%-32s visible: %-5s total: %-5d med: %.2f",
                                    render.getClass().getSimpleName(),
                                    render.isVisible() ? "x" : " ",
                                    render.getCumulateTime() / 1000,
                                    render.getCumulateTime() / 1000 / (double)game.getTick())));
        }
    }

}
