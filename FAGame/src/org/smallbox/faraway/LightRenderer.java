package org.smallbox.faraway;

/**
 * Created by Alex on 04/06/2015.
 */
public abstract class LightRenderer {
    public abstract void onDraw(GFXRenderer renderer, int x, int y);
    public abstract void init();
}
