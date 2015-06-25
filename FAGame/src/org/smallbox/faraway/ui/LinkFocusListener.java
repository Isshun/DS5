package org.smallbox.faraway.ui;

import org.smallbox.faraway.ui.engine.Colors;
import org.smallbox.faraway.ui.engine.OnFocusListener;
import org.smallbox.faraway.ui.engine.TextView;
import org.smallbox.faraway.ui.engine.View;

/**
 * Created by Alex on 25/06/2015.
 */
public class LinkFocusListener implements OnFocusListener {
    @Override
    public void onExit(View view) {
        ((TextView)view).setColor(Colors.LINK_INACTIVE);
        ((TextView)view).setStyle(TextView.REGULAR);
    }
    @Override
    public void onEnter(View view) {
        ((TextView)view).setStyle(TextView.UNDERLINED);
        ((TextView)view).setColor(Colors.LINK_ACTIVE);
    }
}
