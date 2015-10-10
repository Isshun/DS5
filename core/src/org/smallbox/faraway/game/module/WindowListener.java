package org.smallbox.faraway.game.module;

import org.smallbox.faraway.ui.engine.views.UIFrame;

/**
 * Created by Alex on 31/08/2015.
 */
public interface WindowListener {
    void onCreate(UIWindow window, UIFrame view);
    void onRefresh(int update);
    void onClose();
}
