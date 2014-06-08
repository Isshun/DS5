package alone.in.deepspace.util;

public class Settings {

	private static Settings _self;

	public static Settings getInstance() {
		if (_self == null) {
			_self = new Settings();
		}
		return _self;
	}

	private boolean _debug;

	public boolean isDebug() {
		return _debug;
	}

	public void setDebug(boolean b) {
		_debug = b;
	}

}
