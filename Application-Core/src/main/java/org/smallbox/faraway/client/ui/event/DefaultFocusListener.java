package org.smallbox.faraway.client.ui.event;

import org.smallbox.faraway.client.ui.widgets.View;

public class DefaultFocusListener implements OnFocusListener {

    @Override
    public void onEnter(View view) {
        view.setFocus(true);
    }

    @Override
    public void onExit(View view) {
        view.setFocus(false);
    }

}
