package org.smallbox.faraway.client.ui.engine;

import org.smallbox.faraway.client.ui.engine.views.widgets.View;

public interface OnKeyListener {
    void onKeyPress(View view, int key);
    void onKeyRelease(View view, int key);
}
