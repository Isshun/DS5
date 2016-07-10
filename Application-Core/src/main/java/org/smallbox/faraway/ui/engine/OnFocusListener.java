package org.smallbox.faraway.ui.engine;

import org.smallbox.faraway.ui.engine.views.widgets.View;

public interface OnFocusListener {
    void onEnter(View view);
    void onExit(View view);
}