package alone.in.deepspace.UserInterface;

import java.util.HashMap;
import java.util.Map;

import alone.in.deepspace.Engine.ui.OnClickListener;
import alone.in.deepspace.Engine.ui.View;

public class EventManager {
	private static EventManager _self;
	private Map<View, OnClickListener> _onClickListeners;
	private Map<View, OnFocusListener> _onFocusListeners;
	
	private EventManager() {
		_onClickListeners = new HashMap<View, OnClickListener>();
		_onFocusListeners = new HashMap<View, OnFocusListener>();
	}
	
	public static EventManager getInstance() {
		if (_self == null) {
			_self = new EventManager();
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
			if (view.getRect().contains(x, y)) {
				return true;
			}
		}
		return false;
	}

	public void onMouseMove(int x, int y) {
		for (View view: _onFocusListeners.keySet()) {
			if (view.getRect().contains(x, y) && (view.getParent() == null || view.getParent().getVisible())) {
				if (view.isActive() == false) {
					view.setActive(true);
					_onFocusListeners.get(view).onEnter(view);
				}
			} else {
				if (view.isActive()) {
					view.setActive(false);
					_onFocusListeners.get(view).onExit(view);
				}
			}
		}
	}

}
