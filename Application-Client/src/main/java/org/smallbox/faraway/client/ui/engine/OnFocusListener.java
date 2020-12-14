package org.smallbox.faraway.client.ui.engine;

import org.smallbox.faraway.client.ui.engine.views.widgets.View;

public interface OnFocusListener {
    void onEnter(View view);
    void onExit(View view);
}
