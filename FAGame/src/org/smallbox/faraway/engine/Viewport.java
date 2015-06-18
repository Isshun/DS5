package org.smallbox.faraway.engine;

/**
 * Created by Alex on 27/05/2015.
 */
public abstract class Viewport {
    public abstract void update(int x, int y);
    public abstract int getPosX();
    public abstract int getPosY();
    public abstract void setScale(int delta, int x, int y);
    public abstract RenderEffect getRenderEffect();
    public abstract float getScale();
    public abstract float getMinScale();
    public abstract float getMaxScale();
    public abstract void startMove(int x, int y);
}
