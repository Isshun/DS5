package org.smallbox.faraway;

import org.smallbox.faraway.engine.renderer.AreaRenderer;
import org.smallbox.faraway.engine.renderer.IRenderer;
import org.smallbox.faraway.engine.renderer.TemperatureRenderer;
import org.smallbox.faraway.engine.ui.ColorView;
import org.smallbox.faraway.engine.ui.View;

/**
 * Created by Alex on 27/05/2015.
 */
public interface GFXRenderer {
    void draw(SpriteModel sprite, RenderEffect effect);
    void draw(ColorView view, RenderEffect effect);
    void clear(Color color);
    void clear();
    void display();
    void finish();
    void close();
    void refresh();
    void setFullScreen(boolean isFullscreen);
    int getWidth();
    int getHeight();

    void draw(View view, int x, int y);

    AreaRenderer createAreaRenderer();
    TemperatureRenderer createTemperatureRenderer();
    IRenderer createRoomRenderer();
}
