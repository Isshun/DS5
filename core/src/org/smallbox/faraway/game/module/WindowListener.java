package org.smallbox.faraway.game.module;

import org.smallbox.faraway.ui.engine.view.FrameLayout;

/**
 * Created by Alex on 31/08/2015.
 */
public interface WindowListener {
    void onCreate(UIWindow window, FrameLayout view);
    void onRefresh(int update);
    void onClose();
}
