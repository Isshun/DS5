package org.smallbox.faraway.core.ui.engine;


import org.smallbox.faraway.core.ui.engine.views.View;

public interface OnFocusListener {
    void onEnter(View view);
    void onExit(View view);
}
