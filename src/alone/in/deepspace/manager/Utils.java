package alone.in.deepspace.manager;

public class Utils {

	private static int _uuid;

	public static int getUUID() {
		return ++_uuid;
	}

	public static void useUUID(int usedId) {
		if (_uuid < usedId + 1) {
			_uuid = usedId + 1;
		}
	}

}
