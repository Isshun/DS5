package org.smallbox.faraway.core;

import org.smallbox.faraway.core.ui.GDXImageView;
import org.smallbox.faraway.engine.renderer.RenderLayer;
import org.smallbox.faraway.ui.engine.*;
import org.smallbox.faraway.core.ui.GDXColorView;
import org.smallbox.faraway.core.ui.GDXFrameLayout;
import org.smallbox.faraway.core.ui.GDXLabel;

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
    public UILabel createTextView() {
        return new GDXLabel();
    }

    @Override
    public UILabel createTextView(int width, int height) {
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
    public UIImage createImageView() {
        return new GDXImageView(0, 0);
    }

    @Override
    public UIImage createImageView(int width, int height) {
        return new GDXImageView(width, height);
    }

    @Override
    public RenderLayer createRenderLayer(int index, int width, int height) {
        return new GDXRenderLayer(index);
    }
}
