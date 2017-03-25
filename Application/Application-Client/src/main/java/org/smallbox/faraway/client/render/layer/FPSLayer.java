package org.smallbox.faraway.client.render.layer;

import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.client.render.Viewport;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.GameLayer;

@GameLayer(level = 999, visible = true)
public class FPSLayer extends BaseLayer {

    public void onDraw(GDXRenderer renderer, Viewport viewport, double animProgress, int frame) {
        renderer.drawText(10, renderer.getHeight() - 40, 12, Color.RED, "T " + String.valueOf(Application.gameManager.getGame().getTick()));
        renderer.drawText(10, renderer.getHeight() - 25, 12, Color.RED, "F " + String.valueOf(frame));
        renderer.drawPixel(10 + frame / 5 % 32, renderer.getHeight() - 10, 2, 2, Color.RED);
    }

}