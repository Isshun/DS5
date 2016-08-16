package org.smallbox.faraway.ui.engine;

import org.smallbox.faraway.GameEvent;
import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.core.game.GameManager;
import org.smallbox.faraway.ui.UserInterface;
import org.smallbox.faraway.ui.engine.views.widgets.UIDropDown;
import org.smallbox.faraway.ui.engine.views.widgets.View;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;

public class UIEventManager {
    private static UIEventManager           _self;
    private Map<View, OnClickListener>      _onClickListeners;
    private Map<View, OnClickListener>      _onRightClickListeners;
    private Map<View, OnClickListener>      _onMouseWheelUpListeners;
    private Map<View, OnClickListener>      _onMouseWheelDownListeners;
    private Map<View, OnFocusListener>      _onFocusListeners;
    private Map<View, OnKeyListener>        _onKeysListeners;
    private UIDropDown                      _currentDropDown;

    private UIEventManager() {
        _onClickListeners = new ConcurrentSkipListMap<>();
        _onRightClickListeners = new ConcurrentSkipListMap<>();
        _onFocusListeners = new ConcurrentSkipListMap<>();
        _onKeysListeners = new ConcurrentSkipListMap<>();
        _onMouseWheelUpListeners = new ConcurrentSkipListMap<>();
        _onMouseWheelDownListeners = new ConcurrentSkipListMap<>();
    }

    public static UIEventManager getInstance() {
        if (_self == null) {
            _self = new UIEventManager();
        }
        return _self;
    }

    public void setOnFocusListener(View view, OnFocusListener onFocusListener) {
        if (onFocusListener == null) {
            _onFocusListeners.remove(view);
        } else {
            _onFocusListeners.put(view, onFocusListener);
        }
    }

    public void setOnClickListener(View view, OnClickListener onClickListener) {
        if (onClickListener == null) {
            _onClickListeners.remove(view);
        } else {
            _onClickListeners.put(view, onClickListener);
        }
    }

    public void setOnMouseWheelUpListener(View view, OnClickListener onClickListener) {
        if (onClickListener == null) {
            _onMouseWheelUpListeners.remove(view);
        } else {
            _onMouseWheelUpListeners.put(view, onClickListener);
        }
    }

    public void setOnMouseWheelDownListener(View view, OnClickListener onClickListener) {
        if (onClickListener == null) {
            _onMouseWheelDownListeners.remove(view);
        } else {
            _onMouseWheelDownListeners.put(view, onClickListener);
        }
    }

    public void setOnRightClickListener(View view, OnClickListener onClickListener) {
        if (onClickListener == null) {
            _onRightClickListeners.remove(view);
        } else {
            _onRightClickListeners.put(view, onClickListener);
        }
    }

    public boolean click(GameEvent event, int x, int y) {
        View bestView = null;
        int bestDepth = -1;
        boolean gameRunning = GameManager.getInstance().isLoaded();
        for (View view: _onClickListeners.keySet()) {
            if (view.isActive() && (gameRunning || !view.inGame()) && hasVisibleHierarchy(view) && view.contains(x, y) && view.getDeep() > bestDepth) {
                bestDepth = view.getDeep();
                bestView = view;
            }
        }

        if (_currentDropDown != null) {
            _currentDropDown.setOpen(false);
            _currentDropDown = null;
        }

        if (bestView != null) {
            bestView.click(event);
//            _onClickListeners.get(bestView).onClick();
            return true;
        }

        return false;
    }

    public boolean rightClick(GameEvent event, int x, int y) {
        boolean gameRunning = GameManager.getInstance().isLoaded();
        for (View view: _onRightClickListeners.keySet()) {
            if (view.isActive() && (gameRunning || !view.inGame()) && hasVisibleHierarchy(view) && view.contains(x, y)) {
                _onRightClickListeners.get(view).onClick(event);
                return true;
            }
        }
        return false;
    }

    public boolean mouseWheelUp(GameEvent event, int x, int y) {
        boolean gameRunning = GameManager.getInstance().isLoaded();
        for (View view: _onMouseWheelUpListeners.keySet()) {
            if (view.isActive() && (gameRunning || !view.inGame()) && hasVisibleHierarchy(view) && view.contains(x, y)) {
                _onMouseWheelUpListeners.get(view).onClick(event);
                return true;
            }
        }
        return false;
    }

    public boolean mouseWheelDown(GameEvent event, int x, int y) {
        boolean gameRunning = GameManager.getInstance().isLoaded();
        for (View view: _onMouseWheelDownListeners.keySet()) {
            if (view.isActive() && (gameRunning || !view.inGame()) && hasVisibleHierarchy(view) && view.contains(x, y)) {
                _onMouseWheelDownListeners.get(view).onClick(event);
                return true;
            }
        }
        return false;
    }

    public boolean keyRelease(GameEventListener.Key key) {
        boolean gameRunning = GameManager.getInstance().isLoaded();
        for (View view: _onKeysListeners.keySet()) {
            if (view.isActive() && (gameRunning || !view.inGame()) && hasVisibleHierarchy(view) && hasFocus(view)) {
                _onKeysListeners.get(view).onKeyRelease(view, key);
                return true;
            }
        }
        return false;
    }

    private boolean hasFocus(View view) {
        return true;
    }

    public void onMouseMove(int x, int y) {
        boolean gameRunning = GameManager.getInstance().isLoaded();

        UserInterface.getInstance().getViews().stream()
                .filter(view -> view.isVisible() && view.isActive() && (gameRunning || !view.inGame()) && hasVisibleHierarchy(view))
                .forEach(view -> {
                    if (hasVisibleHierarchy(view) && view.contains(x, y)) {
                        view.onEnter();
                    } else if (view.isFocus()) {
                        view.onExit();
                    }
                });
    }

    public boolean has(int x, int y) {
        for (View view: _onClickListeners.keySet()) {
            if (hasVisibleHierarchy(view) && view.contains(x, y)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasVisibleHierarchy(View view) {
        while (view != null) {
            if (!view.isVisible()) {
                return false;
            }
            if (!view.isActive()) {
                return false;
            }
            view = view.getParent();
        }
        return true;
    }

    public void removeOnKeyListener(View view) {
        _onKeysListeners.remove(view);
    }

    public void removeOnClickListener(View view) {
        _onClickListeners.remove(view);
    }

    public void removeOnFocusListener(View view) {
        _onFocusListeners.remove(view);
    }

    public void clear() {
        _onClickListeners.clear();
        _onRightClickListeners.clear();
        _onFocusListeners.clear();
        _onKeysListeners.clear();
        _onMouseWheelDownListeners.clear();
        _onMouseWheelUpListeners.clear();
    }

    public void removeListeners(List<View> views) {
        views.forEach(view -> {
            _onRightClickListeners.remove(view);
            _onRightClickListeners.remove(view);
            _onClickListeners.remove(view);
            _onFocusListeners.remove(view);
            _onKeysListeners.remove(view);
            _onMouseWheelDownListeners.remove(view);
            _onMouseWheelUpListeners.remove(view);
        });
    }
    public void removeListeners(View view) {
        _onRightClickListeners.remove(view);
        _onRightClickListeners.remove(view);
        _onClickListeners.remove(view);
        _onFocusListeners.remove(view);
        _onKeysListeners.remove(view);
        _onMouseWheelDownListeners.remove(view);
        _onMouseWheelUpListeners.remove(view);
    }

    public void setOnKeyListener(View view, OnKeyListener onKeyListener) {
        _onKeysListeners.put(view, onKeyListener);
    }

    public void setCurrentDropDown(UIDropDown dropDown) {
        _currentDropDown = dropDown;
    }
}