package org.smallbox.faraway.client.ui.event;

import org.smallbox.faraway.client.ui.widgets.View;

public interface OnFocusListener {
    void onEnter(View view);
    void onExit(View view);
}
