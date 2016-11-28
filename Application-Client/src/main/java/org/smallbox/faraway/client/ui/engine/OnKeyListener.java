package org.smallbox.faraway.client.ui.engine;

import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.client.ui.engine.views.widgets.View;

/**
 * Created by Alex on 14/11/2015.
 */
public interface OnKeyListener {
    void onKeyPress(View view, GameEventListener.Key key);
    void onKeyRelease(View view, GameEventListener.Key key);
}
