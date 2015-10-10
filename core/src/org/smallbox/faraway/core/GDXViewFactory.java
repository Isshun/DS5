package org.smallbox.faraway.core;

import org.smallbox.faraway.ui.engine.ViewFactory;
import org.smallbox.faraway.ui.engine.views.UIFrame;
import org.smallbox.faraway.ui.engine.views.UIImage;
import org.smallbox.faraway.ui.engine.views.UILabel;

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
    public UIImage createImageView() {
        return new UIImage(-1, 1);
    }
}
