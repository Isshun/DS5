package org.smallbox.faraway;

/**
 * Created by Alex on 04/06/2015.
 */
public abstract class LightRenderer implements GameObserver {
    public abstract void onDraw(GFXRenderer renderer, int x, int y);
    public abstract void init();
    public abstract void setSunColor(Color color);
}
