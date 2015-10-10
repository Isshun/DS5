package org.smallbox.faraway.ui.engine;

import org.smallbox.faraway.ui.engine.views.View;

import java.util.HashMap;
import java.util.Map;



public class UIEventManager {
	private static UIEventManager 		_self;
	private Map<View, OnClickListener> 	_onClickListeners;
	private Map<View, OnClickListener> 	_onRightClickListeners;
	private Map<View, OnFocusListener> 	_onFocusListeners;

	private UIEventManager() {
		_onClickListeners = new HashMap<>();
		_onRightClickListeners = new HashMap<>();
		_onFocusListeners = new HashMap<>();
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
		for (View view: _onClickListeners.keySet()) {
			if (hasVisibleHierarchy(view) && view.contains(x, y)) {
				_onClickListeners.get(view).onClick(view);
				return true;
			}
		}
		return false;
	}

	public boolean rightClick(int x, int y) {
		for (View view: _onRightClickListeners.keySet()) {
			if (hasVisibleHierarchy(view) && view.contains(x, y)) {
				_onRightClickListeners.get(view).onClick(view);
				return true;
			}
		}
		return false;
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
			view = view.getParent();
		}
		return true;
	}

	public void onMouseMove(int x, int y) {
		for (View view: _onFocusListeners.keySet()) {
			if (hasVisibleHierarchy(view) && view.contains(x, y)) {
                view.onEnter();
			}
            else if (view.isFocus()) {
                view.onExit();
            }
		}
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
	}

	public void removeListeners(View view) {
		_onRightClickListeners.remove(view);
		_onClickListeners.remove(view);
		_onFocusListeners.remove(view);
	}
}
