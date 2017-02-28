package org.smallbox.faraway.client.renderer;

import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.core.GameRenderer;

@GameRenderer(level = 999, visible = true)
public class FPSRenderer extends BaseRenderer {

    public void    onDraw(GDXRenderer renderer, Viewport viewport, double animProgress, int frame) {
        renderer.drawText(10, renderer.getHeight() - 25, 12, Color.RED, String.valueOf(frame));
        renderer.drawPixel(10 + frame / 5 % 32, renderer.getHeight() - 10, 2, 2, Color.RED);
    }

}