package alone.in.deepspace.UserInterface;

import java.util.HashMap;
import java.util.Map;

import alone.in.deepspace.UserInterface.Utils.OnClickListener;
import alone.in.deepspace.UserInterface.Utils.UIView;

public class EventManager {
	private static EventManager _self;
	private Map<UIView, OnClickListener> _onClickListeners;
	
	private EventManager() {
		_onClickListeners = new HashMap<UIView, OnClickListener>();
	}
	
	public static EventManager getInstance() {
		if (_self == null) {
			_self = new EventManager();
		}
		return _self;
	}

	public void setOnClickListener(UIView view, OnClickListener onClickListener) {
		if (onClickListener == null) {
			_onClickListeners.remove(view);
		} else {
			_onClickListeners.put(view, onClickListener);
		}
	}
	
	public boolean leftClick(int x, int y) {
		for (UIView view: _onClickListeners.keySet()) {
			if (view.getRect().contains(x, y) && (view.getParent() == null || view.getParent().getVisible())) {
				_onClickListeners.get(view).onClick(view);
				return true;
			}
		}
		return false;
	}

	public boolean has(int x, int y) {
		for (UIView view: _onClickListeners.keySet()) {
			if (view.getRect().contains(x, y)) {
				return true;
			}
		}
		return false;
	}

}
