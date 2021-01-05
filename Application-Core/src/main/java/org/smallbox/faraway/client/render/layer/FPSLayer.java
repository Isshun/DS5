package org.smallbox.faraway.client.render.layer;

import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.client.render.Viewport;
import org.smallbox.faraway.core.GameLayer;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.Game;

@GameObject
@GameLayer(level = 999, visible = true)
public class FPSLayer extends BaseLayer {

    @Inject
    private Game game;

    public void onDraw(GDXRenderer renderer, Viewport viewport, double animProgress, int frame) {
        long heapSize = Runtime.getRuntime().totalMemory();
        long heapFreeSize = Runtime.getRuntime().freeMemory();

        renderer.drawText(10, 10, 12, Color.RED, "Heap: " + (heapSize - heapFreeSize) / 1000 / 1000);
        renderer.drawText(10, 25, 12, Color.RED, "T " + game.getTick());
        renderer.drawText(10, 40, 12, Color.RED, "F " + frame);
        renderer.drawPixel(10 + frame / 5 % 32, 55, 2, 2, Color.RED);
    }

}