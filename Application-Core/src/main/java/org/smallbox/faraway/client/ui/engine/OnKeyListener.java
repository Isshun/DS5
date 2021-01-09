package org.smallbox.faraway.client.ui.engine;

import org.smallbox.faraway.client.ui.engine.views.View;

public interface OnKeyListener {
    void onKeyPress(View view, int key);
    void onKeyRelease(View view, int key);
}
