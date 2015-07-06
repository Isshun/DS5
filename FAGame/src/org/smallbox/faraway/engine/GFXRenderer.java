package org.smallbox.faraway.engine;

import org.smallbox.faraway.engine.renderer.BaseRenderer;
import org.smallbox.faraway.ui.engine.ColorView;
import org.smallbox.faraway.ui.engine.View;

/**
 * Created by Alex on 27/05/2015.
 */
public interface GFXRenderer {
    void draw(View view, int x, int y);
    void draw(SpriteModel sprite, int x, int y);
    void draw(ColorView view, RenderEffect effect);
    void draw(Color color, int x, int y, int width, int height);
    void clear(Color color);
    void clear();
    void display();
    void finish();
    void close();
    void refresh();
    void setFullScreen(boolean isFullscreen);
    int getWidth();
    int getHeight();

    BaseRenderer createAreaRenderer();
    BaseRenderer createTemperatureRenderer();
    BaseRenderer createRoomRenderer();
    BaseRenderer createFaunaRenderer();

    void zoomUp();
    void zoomDown();

}
