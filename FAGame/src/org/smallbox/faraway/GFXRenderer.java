package org.smallbox.faraway;

import org.smallbox.faraway.engine.ui.ColorView;

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
    boolean isOpen();
    void refresh();
    void setFullScreen(boolean isFullscreen);
    void drawLight();
    GameTimer getTimer();
    int getWidth();
    int getHeight();
}
