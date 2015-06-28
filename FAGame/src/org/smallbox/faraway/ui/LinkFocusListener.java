package org.smallbox.faraway.ui;

import org.smallbox.faraway.ui.engine.Colors;
import org.smallbox.faraway.ui.engine.OnFocusListener;
import org.smallbox.faraway.ui.engine.UILabel;
import org.smallbox.faraway.ui.engine.View;

/**
 * Created by Alex on 25/06/2015.
 */
public class LinkFocusListener implements OnFocusListener {
    @Override
    public void onExit(View view) {
        ((UILabel)view).setColor(Colors.LINK_INACTIVE);
        ((UILabel)view).setStyle(UILabel.REGULAR);
    }
    @Override
    public void onEnter(View view) {
        ((UILabel)view).setStyle(UILabel.UNDERLINED);
        ((UILabel)view).setColor(Colors.LINK_ACTIVE);
    }
}
