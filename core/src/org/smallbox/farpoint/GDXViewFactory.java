package org.smallbox.farpoint;

import org.smallbox.faraway.engine.renderer.RenderLayer;
import org.smallbox.faraway.engine.ui.*;
import org.smallbox.farpoint.ui.GDXColorView;
import org.smallbox.farpoint.ui.GDXFrameLayout;
import org.smallbox.farpoint.ui.GDXImageView;
import org.smallbox.farpoint.ui.GDXLabel;

/**
 * Created by Alex on 04/06/2015.
 */
public class GDXViewFactory extends ViewFactory {
    @Override
    public FrameLayout createFrameLayout(int width, int height) {
        GDXFrameLayout layout = new GDXFrameLayout(width, height);
        layout.setSize(width, height);
        return layout;
    }

    @Override
    public FrameLayout createFrameLayout() {
        return new GDXFrameLayout(0, 0);
    }

    @Override
    public TextView createTextView() {
        return new GDXLabel();
    }

    @Override
    public TextView createTextView(int width, int height) {
        GDXLabel label = new GDXLabel();
        label.setSize(width, height);
        return label;
    }

    @Override
    public ColorView createColorView() {
        return new GDXColorView(100, 100);
    }

    @Override
    public ColorView createColorView(int width, int height) {
        GDXColorView view = new GDXColorView(width, height);
        view.setSize(width, height);
        return view;
    }

    @Override
    public ImageView createImageView() {
        return new GDXImageView(0, 0);
    }

    @Override
    public ImageView createImageView(int width, int height) {
        return new GDXImageView(width, height);
    }

    @Override
    public RenderLayer createRenderLayer(int width, int height) {
        return new GDXRenderLayer();
    }
}
