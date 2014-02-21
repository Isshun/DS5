package alone.in.deepspace.Utils;

import alone.in.deepspace.UserInterface.UserInterface;


@SuppressWarnings("unused")
public class Log {
	private static int LEVEL_FATAL = 0;
	private static int LEVEL_ERROR = 1;
	private static int LEVEL_WARNING = 2;
	private static int LEVEL_INFO = 3;
	private static int LEVEL_DEBUG = 4;
	private static int LEVEL = LEVEL_INFO;
	
	public static void debug(String str) {
		if (LEVEL < LEVEL_DEBUG) return;
		
		if (str != null) {
			System.out.println(str);
		}
	}

	public static void error(String str) {
		if (LEVEL < LEVEL_ERROR) return;

		if (str != null) {
			System.out.println(str);
		}
		
		UserInterface.getInstance().displayMessage(str);
	}

	public static void info(String str) {
		if (LEVEL < LEVEL_INFO) return;

		if (str != null) {
			System.out.println(str);
		}
	}

	public static void warning(String str) {
		if (LEVEL < LEVEL_WARNING) return;

		if (str != null) {
			System.out.println(str);
		}
	}
}
