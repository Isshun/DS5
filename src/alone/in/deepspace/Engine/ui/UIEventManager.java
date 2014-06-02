package alone.in.deepspace.engine.ui;

import java.util.HashMap;
import java.util.Map;



public class UIEventManager {
	private static UIEventManager _self;
	private Map<View, OnClickListener> _onClickListeners;
	private Map<View, OnFocusListener> _onFocusListeners;
	
	private UIEventManager() {
		_onClickListeners = new HashMap<View, OnClickListener>();
		_onFocusListeners = new HashMap<View, OnFocusListener>();
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
	
	public boolean leftClick(int x, int y) {
		for (View view: _onClickListeners.keySet()) {
			if (view.getRect().contains(x, y) && (view.getParent() == null || view.getParent().getVisible())) {
				_onClickListeners.get(view).onClick(view);
				return true;
			}
		}
		return false;
	}

	public boolean has(int x, int y) {
		for (View view: _onClickListeners.keySet()) {
			if (hasVisibleHierarchy(view) && view.getRect().contains(x, y)) {
				return true;
			}
		}
		return false;
	}

	private boolean hasVisibleHierarchy(View view) {
		while (view != null) {
			if (view.isVisible() == false) {
				return false;
			}
			view = view.getParent();
		}
		return true;
	}

	public void onMouseMove(int x, int y) {
		for (View view: _onFocusListeners.keySet()) {
			if (view.getRect().contains(x, y) && (view.getParent() == null || view.getParent().getVisible())) {
				if (view.isFocus() == false) {
					view.onEnter();
				}
			} else {
				if (view.isFocus()) {
					view.onExit();
				}
			}
		}
	}

	public void removeOnClickListener(OnClickListener listener) {
		_onClickListeners.remove(listener);
	}

	public void removeOnFocusListener(OnFocusListener listener) {
		_onFocusListeners.remove(listener);
	}

}
