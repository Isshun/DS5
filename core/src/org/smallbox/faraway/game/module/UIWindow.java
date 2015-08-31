package org.smallbox.faraway.game.module;

import org.smallbox.faraway.engine.GameEventListener;
import org.smallbox.faraway.ui.engine.LayoutFactory;
import org.smallbox.faraway.ui.engine.view.FrameLayout;

/**
 * Created by Alex on 31/08/2015.
 */
public abstract class UIWindow extends FrameLayout {
    protected FrameLayout             _frameMain;
    protected FrameLayout             _frameContent;

    public UIWindow() {
        _frameContent = this;
        if (getContentLayout() != null) {
            LayoutFactory.load("data/ui/" + getContentLayout(), this, null);
        }
    }

    protected abstract void onCreate(UIWindow window, FrameLayout content);
    protected abstract void onRefresh(int update);
    protected abstract String getContentLayout();

    public void create() {
        onCreate(this, _frameContent);
    }

    public void refresh(int update) {
        onRefresh(update);
    }

    @Override
    public void refresh() {
    }

    @Override
    public int getContentWidth() {
        return 400;
    }

    @Override
    public int getContentHeight() {
        return 400;
    }

    public boolean onMouseEvent(GameEventListener.Action action, GameEventListener.MouseButton button, int x, int y) {
        return false;
    }

}
