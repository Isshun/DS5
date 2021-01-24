package org.smallbox.faraway.client.ui.event;

import org.smallbox.faraway.client.ui.widgets.View;

public interface OnKeyListener {
    void onKeyPress(View view, int key);
    void onKeyRelease(View view, int key);
}
