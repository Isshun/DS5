package org.smallbox.faraway.engine.ui;

import org.smallbox.faraway.engine.renderer.RenderLayer;

/**
 * Created by Alex on 27/05/2015.
 */
public abstract class ViewFactory {
    private static ViewFactory _factory;

    public static void setInstance(ViewFactory factory) {
        _factory = factory;
    }

    public static ViewFactory getInstance() {
        return _factory;
    }

    public abstract TextView createTextView();
    public abstract TextView createTextView(int width, int height);
    public abstract ColorView createColorView();
    public abstract ColorView createColorView(int width, int height);
    public abstract FrameLayout createFrameLayout();
    public abstract FrameLayout createFrameLayout(int width, int height);
    public abstract ImageView createImageView();
    public abstract RenderLayer createRenderLayer(int width, int height);
}
