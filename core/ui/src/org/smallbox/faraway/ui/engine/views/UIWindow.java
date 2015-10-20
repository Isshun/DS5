package org.smallbox.faraway.ui.engine.views;

import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.ui.engine.LayoutFactory;
import org.smallbox.faraway.ui.engine.OnClickListener;
import org.smallbox.faraway.ui.engine.ViewFactory;

/**
 * Created by Alex on 31/08/2015.
 */
public abstract class UIWindow extends UIFrame {
    protected UIFrame _frameMain;
    protected UIFrame _frameContent;
    private boolean             _isLoaded;
    private int                 _debugIndex;

    public UIWindow() {
        _frameContent = this;
        if (getContentLayout() != null) {
            LayoutFactory.load("data/ui/" + getContentLayout(), this, null);
        }
        _isLoaded = true;
    }

    protected abstract void onCreate(UIWindow window, UIFrame content);
    protected abstract void onRefresh(int update);
    protected abstract String getContentLayout();

    public void create() {
        onCreate(this, _frameContent);
    }

    public void refresh(int update) {
        if (_isVisible && _isLoaded) {
            onRefresh(update);
        }
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

    protected void addDebugView(UIFrame frame, String text) {
        addDebugView(frame, text, null);
    }

    protected void addDebugView(UIFrame frame, String text, OnClickListener clickListener) {
        UILabel lbCommand = ViewFactory.getInstance().createTextView();
        lbCommand.setText(text);
        lbCommand.setTextSize(14);
        lbCommand.setPosition(6, 38 + 20 * _debugIndex++);
        lbCommand.setSize(230, 20);
        lbCommand.setOnClickListener(clickListener);
        frame.addView(lbCommand);
    }

}
