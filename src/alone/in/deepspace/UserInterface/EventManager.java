package alone.in.DeepSpace.UserInterface;

import java.util.HashMap;
import java.util.Map;

import alone.in.DeepSpace.UserInterface.Utils.OnClickListener;
import alone.in.DeepSpace.UserInterface.Utils.UIView;

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
		_onClickListeners.put(view, onClickListener);
	}
	
	public boolean rightClick(int x, int y) {
		for (UIView view: _onClickListeners.keySet()) {
			if (view.getRect().contains(x, y)) {
				_onClickListeners.get(view).onClick(view);
				return true;
			}
		}
		return false;
	}

}
