package org.smallbox.faraway.core;

import org.smallbox.faraway.core.ui.GDXColorView;
import org.smallbox.faraway.core.ui.GDXImageView;
import org.smallbox.faraway.ui.engine.ViewFactory;
import org.smallbox.faraway.ui.engine.view.ColorView;
import org.smallbox.faraway.ui.engine.view.UIFrame;
import org.smallbox.faraway.ui.engine.view.UIImage;
import org.smallbox.faraway.ui.engine.view.UILabel;

/**
 * Created by Alex on 04/06/2015.
 */
public class GDXViewFactory extends ViewFactory {
    @Override
    public UIFrame createFrameLayout(int width, int height) {
        UIFrame layout = new UIFrame(width, height);
        layout.setSize(width, height);
        return layout;
    }

    @Override
    public UIFrame createFrameLayout() {
        return new UIFrame(0, 0);
    }

    @Override
    public UILabel createTextView() {
        return new UILabel();
    }

    @Override
    public UILabel createTextView(int width, int height) {
        UILabel label = new UILabel();
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
}
