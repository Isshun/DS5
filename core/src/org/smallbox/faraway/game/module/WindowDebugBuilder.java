package org.smallbox.faraway.game.module;

import org.smallbox.faraway.ui.engine.view.FrameLayout;

/**
 * Created by Alex on 31/08/2015.
 */
public class WindowDebugBuilder extends WindowBuilder {

    public static WindowBuilder create() {
        return new WindowDebugBuilder();
    }

    public DebugWindow build(WindowListener listener) {
        return new DebugWindow() {
            @Override
            public void onCreate(UIWindow window, FrameLayout view) {
                listener.onCreate(window, view);
            }

            @Override
            protected void onRefresh(int update) {
                listener.onRefresh(update);
            }

            @Override
            protected void onClose() {
                listener.onClose();
            }

            @Override
            protected boolean isClosable() {
                return _isClosable;
            }

            @Override
            protected boolean isMovable() {
                return _isMovable;
            }

            @Override
            protected String getContentLayout() {
                return _contentLayout;
            }

            @Override
            protected String getTitle() {
                return _title;
            }
        };
    }
}
