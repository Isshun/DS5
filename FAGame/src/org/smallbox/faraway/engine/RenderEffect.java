package org.smallbox.faraway.engine;

/**
 * Created by Alex on 27/05/2015.
 */
public abstract class RenderEffect {
    private Viewport _viewport;

    public abstract void setTranslate(int x, int y);

    public void setViewport(Viewport viewport) {
        _viewport = viewport;
    }

    public Viewport getViewport() {
        return _viewport;
    }
}
