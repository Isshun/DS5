package org.smallbox.faraway.client.ui;

import org.smallbox.faraway.client.ui.engine.Colors;
import org.smallbox.faraway.client.ui.engine.OnFocusListener;
import org.smallbox.faraway.client.ui.engine.views.View;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;

public class LinkFocusListener implements OnFocusListener {
    @Override
    public void onExit(View view) {
        ((UILabel)view).setTextColor(Colors.LINK_INACTIVE);
        ((UILabel)view).setStyle(UILabel.REGULAR);
    }
    @Override
    public void onEnter(View view) {
        ((UILabel)view).setStyle(UILabel.UNDERLINED);
        ((UILabel)view).setTextColor(Colors.LINK_ACTIVE);
    }
}
