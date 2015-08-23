package org.smallbox.faraway.ui.panel.debug;

import org.smallbox.faraway.engine.Color;
import org.smallbox.faraway.engine.GameEventListener;
import org.smallbox.faraway.ui.LayoutModel;
import org.smallbox.faraway.ui.engine.OnClickListener;
import org.smallbox.faraway.ui.engine.ViewFactory;
import org.smallbox.faraway.ui.engine.view.FrameLayout;
import org.smallbox.faraway.ui.engine.view.UILabel;
import org.smallbox.faraway.ui.panel.BasePanel;

/**
 * Created by Alex on 13/07/2015.
 */
public abstract class BaseDebugPanel extends BasePanel {
    private FrameLayout _frame;
    private UILabel _lbClose;
    private UILabel             _lbTitle;
    private boolean             _isMoving;
    private int                 _movingOffsetX;
    private int                 _movingOffsetY;

    public BaseDebugPanel() {
        super(null, null, 200, 200, 250, 350, "data/ui/panels/debug_manager.yml");
        setAlwaysVisible(true);
        setVisible(false);
    }

    @Override
    public void onLayoutLoaded(LayoutModel layout) {
        _frame = (FrameLayout)findById("frame_dev_commands");
    }

    @Override
    protected void onRefresh(int update) {
        _frame.removeAllViews();

        _lbTitle = ViewFactory.getInstance().createTextView();
        _lbTitle.setString(" " + getTitle());
        _lbTitle.setCharacterSize(14);
        _lbTitle.setPosition(0, 0);
        _lbTitle.setSize(_width, 28);
        _lbTitle.setBackgroundColor(new Color(0x11344f));
        _lbTitle.setAlign(Align.CENTER_VERTICAL);
        _frame.addView(_lbTitle);
        _lbTitle.resetAllPos();

        _lbClose = ViewFactory.getInstance().createTextView();
        _lbClose.setString("[x]");
        _lbClose.setCharacterSize(14);
        _lbClose.setPosition(220, 0);
        _lbClose.setSize(28, 28);
        _lbClose.setAlign(Align.CENTER_VERTICAL);
        _lbClose.setOnClickListener(view -> setVisible(false));
        _frame.addView(_lbClose);
        _lbClose.resetAllPos();

        onAddDebug();
        setSize(250, 38 + 20 * _debugIndex);
    }

    protected abstract String getTitle();

    protected abstract void onAddDebug();

    protected void addDebugView(String text) {
        addDebugView(_frame, text, null);
    }

    protected void addDebugView(String text, OnClickListener clickListener) {
        addDebugView(_frame, text, clickListener);
    }

    public boolean onMouseEvent(GameEventListener.Action action, GameEventListener.MouseButton button, int x, int y) {
        if (_isVisible) {
            if (!_isMoving && action == GameEventListener.Action.PRESSED && _lbTitle.getRect().contains(x, y) && !_lbClose.getRect().contains(x, y)) {
                _movingOffsetX = x - _x;
                _movingOffsetY = y - _y;
                _isMoving = true;
                return true;
            }
            if (_isMoving && action == GameEventListener.Action.RELEASED) {
                _isMoving = false;
                resetAllPos();
                return true;
            }
            if (_isMoving && action == GameEventListener.Action.MOVE) {
                System.out.println("move !");
                setPosition(x - _movingOffsetX, y - _movingOffsetY);
                return true;
            }
        }
        return false;
    }

}
