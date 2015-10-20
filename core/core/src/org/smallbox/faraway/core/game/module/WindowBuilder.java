package org.smallbox.faraway.core.game.module;

import org.smallbox.faraway.core.engine.renderer.GDXRenderer;
import org.smallbox.faraway.ui.engine.views.UIFrame;

/**
 * Created by Alex on 31/08/2015.
 */
public class WindowBuilder {
    protected String    _title;
    protected String    _contentLayout;
    protected boolean   _isMovable;
    protected boolean   _isClosable;

    public UITitleWindow build(WindowListener listener) {
        return new UITitleWindow() {
            @Override
            public void draw(GDXRenderer renderer, int x, int y) {
            }

            @Override
            protected void onCreate(UIWindow window, UIFrame content) {
                listener.onCreate(window, content);
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

    public static WindowBuilder create() {
        return new WindowBuilder();
    }

    public WindowBuilder setTitle(String title) {
        _title = title;
        return this;
    }

    public WindowBuilder setContentLayout(String contentLayout) {
        _contentLayout = contentLayout;
        return this;
    }

    public WindowBuilder setMovable(boolean isMovable) {
        _isMovable = isMovable;
        return this;
    }

    public WindowBuilder setClosable(boolean isClosable) {
        _isClosable = isClosable;
        return this;
    }
}
