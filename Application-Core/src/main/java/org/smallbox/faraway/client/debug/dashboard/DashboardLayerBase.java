package org.smallbox.faraway.client.debug.dashboard;

import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.client.render.GDXRenderer;

public abstract class DashboardLayerBase {

    private int index;

    public void draw(GDXRenderer renderer, int frame) {
        index = 5;
        onDraw(renderer, frame);
    }

    protected abstract void onDraw(GDXRenderer renderer, int frame);

    protected void drawDebug(GDXRenderer renderer, String label, Object object) {
        renderer.drawTextUI(12, (index * 20) + 12, 18, Color.BLACK, "[" + label.toUpperCase() + "] " + object);
        renderer.drawTextUI(11, (index * 20) + 11, 18, Color.BLACK, "[" + label.toUpperCase() + "] " + object);
        renderer.drawTextUI(10, (index * 20) + 10, 18, Color.WHITE, "[" + label.toUpperCase() + "] " + object);
        index++;
    }

}
