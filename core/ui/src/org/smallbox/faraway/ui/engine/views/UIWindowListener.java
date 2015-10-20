package org.smallbox.faraway.ui.engine.views;

/**
 * Created by Alex on 31/08/2015.
 */
public interface UIWindowListener {
    void onCreate(UIWindow window, UIFrame view);
    void onRefresh(int update);
    void onClose();
}
