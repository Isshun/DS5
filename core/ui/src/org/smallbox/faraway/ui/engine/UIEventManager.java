package org.smallbox.faraway.ui.engine;

import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.core.game.GameManager;
import org.smallbox.faraway.ui.engine.views.widgets.View;

import java.util.*;

public class UIEventManager {
    private static UIEventManager           _self;
    private Map<View, OnClickListener>      _onClickListeners;
    private Map<View, OnClickListener>      _onRightClickListeners;
    private Map<View, OnFocusListener>      _onFocusListeners;
    private Map<View, OnKeyListener>        _onKeysListeners;

    private UIEventManager() {
        _onClickListeners = new TreeMap<>((Comparator<View>) (v1, v2) -> v2.compareLevel(v1));
        _onRightClickListeners = new TreeMap<>((Comparator<View>) (v1, v2) -> v2.compareLevel(v1));
        _onFocusListeners = new TreeMap<>((Comparator<View>) (v1, v2) -> v2.compareLevel(v1));
        _onKeysListeners = new TreeMap<>((Comparator<View>) (v1, v2) -> v2.compareLevel(v1));
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

    public void setOnRightClickListener(View view, OnClickListener onClickListener) {
        if (onClickListener == null) {
            _onRightClickListeners.remove(view);
        } else {
            _onRightClickListeners.put(view, onClickListener);
        }
    }

    public boolean click(int x, int y) {
        boolean gameRunning = GameManager.getInstance().isLoaded();
        for (View view: _onClickListeners.keySet()) {
            if (view.isActive() && (gameRunning || !view.inGame()) && hasVisibleHierarchy(view) && view.contains(x, y)) {
                _onClickListeners.get(view).onClick();
                return true;
            }
        }
        return false;
    }

    public boolean rightClick(int x, int y) {
        boolean gameRunning = GameManager.getInstance().isLoaded();
        for (View view: _onRightClickListeners.keySet()) {
            if (view.isActive() && (gameRunning || !view.inGame()) && hasVisibleHierarchy(view) && view.contains(x, y)) {
                _onRightClickListeners.get(view).onClick();
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
        for (View view: _onFocusListeners.keySet()) {
            if (view.isActive() && (gameRunning || !view.inGame()) && hasVisibleHierarchy(view)) {
                if (hasVisibleHierarchy(view) && view.contains(x, y)) {
                    view.onEnter();
                } else if (view.isFocus()) {
                    view.onExit();
                }
            }
        }
    }

    public boolean has(int x, int y) {
        for (View view: _onClickListeners.keySet()) {
            if (hasVisibleHierarchy(view) && view.contains(x, y)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasVisibleHierarchy(View view) {
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
    }

    public void removeListeners(View view) {
        _onRightClickListeners.remove(view);
        _onClickListeners.remove(view);
        _onFocusListeners.remove(view);
        _onKeysListeners.remove(view);
    }

    public void setOnKeyListener(View view, OnKeyListener onKeyListener) {
        _onKeysListeners.put(view, onKeyListener);
    }
}