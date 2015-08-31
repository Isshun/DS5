package org.smallbox.faraway.game.module;

import org.smallbox.faraway.engine.GameEventListener;
import org.smallbox.faraway.ui.engine.LayoutFactory;
import org.smallbox.faraway.ui.engine.view.FrameLayout;
import org.smallbox.faraway.ui.engine.view.UILabel;

/**
 * Created by Alex on 13/07/2015.
 */
public abstract class UITitleWindow extends UIWindow {
//    private FrameLayout             _frameContent;
    private final boolean           _isMovable;
    private UILabel                 _lbClose;
    private UILabel                 _lbTitle;
    private boolean                 _isMoving;
    private int                     _movingOffsetX;
    private int                     _movingOffsetY;

    public UITitleWindow() {
        final String title = getTitle();
        final String mainLayout = title != null ? "data/ui/base/floating_title_window.yml" : "data/ui/base/floating_window.yml";
        final String contentLayout = getContentLayout();
        _isMovable = isMovable();
        LayoutFactory.load(mainLayout, this, (l1, p1) -> {
            _frameMain = p1;

            if (title != null) {
                _lbClose = (UILabel) findById("lb_close");
                _lbClose.setVisible(isClosable());
                if (isClosable()) {
                    _lbClose.setOnClickListener(view -> onClose());
                }
                _lbTitle = (UILabel) findById("lb_title");
                _lbTitle.setString(" " + title);
            }

            _frameContent = (FrameLayout) findById("frame_content");

            if (contentLayout != null) {
                LayoutFactory.load("data/ui/" + contentLayout, _frameContent, null);
            }
        });
    }

    public void create() {
        super.create();
        _frameMain.setSize(250, _frameContent.getContentHeight() + (_lbTitle != null ? 28 : 0));
    }

    public void refresh(int update) {
        super.refresh(update);

        if (_lbTitle != null) {
            _lbTitle.resetAllPos();
        }
        if (_lbClose != null) {
            _lbClose.resetAllPos();
        }
    }

    @Override
    public void refresh() {
    }

    @Override
    public int getContentWidth() {
        return 200;
    }

    @Override
    public int getContentHeight() {
        return 200;
    }

    @Override
    public boolean onMouseEvent(GameEventListener.Action action, GameEventListener.MouseButton button, int x, int y) {
        if (_isMovable && _isVisible && _lbTitle != null) {
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

    protected abstract String getTitle();
    protected abstract boolean isClosable();
    protected abstract boolean isMovable();
    protected abstract void onClose();
}
