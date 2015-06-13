package org.smallbox.faraway.engine.util;

import org.smallbox.faraway.ui.UserInterface;

public class Log {
	public static int LEVEL_FATAL = 0;
	public static int LEVEL_ERROR = 1;
	public static int LEVEL_WARNING = 2;
	public static int LEVEL_INFO = 3;
	public static int LEVEL_DEBUG = 4;
	public static int LEVEL = LEVEL_DEBUG;
	
	public static void debug(String str) {
		if (LEVEL < LEVEL_DEBUG) return;
		println(LEVEL_DEBUG, str);
	}

	public static void info(String str) {
		if (LEVEL < LEVEL_INFO) return;
		println(LEVEL_INFO, str);
	}

	public static void warning(String str) {
		if (LEVEL < LEVEL_WARNING) return;
		println(LEVEL_WARNING, str);
	}

	public static void error(String str) {
		if (LEVEL < LEVEL_ERROR) return;
		println(LEVEL_ERROR, str);

		if (str != null && UserInterface.getInstance() != null) {
			UserInterface.getInstance().displayMessage(str);
		}

		// Print stack
		StackTraceElement[] elements = Thread.currentThread().getStackTrace();
		Log.debug("Error occured \"" + str + "\"");
		for (int i = 2; i < elements.length; i++) {
			Log.debug("          " + elements[i].toString());
		}
	}

	private static void println(int strLevel, String str) {
		if (str != null) {
			System.out.println(str);

			if (UserInterface.getInstance() != null) {
				UserInterface.getInstance().addMessage(strLevel, str);
			}
		}
	}

	public static String getPrefix(int level) {
		if (level == LEVEL_DEBUG) return "[D] ";
		if (level == LEVEL_INFO) return "[I] ";
		if (level == LEVEL_WARNING) return "[W] ";
		if (level == LEVEL_ERROR) return "[E] ";
		if (level == LEVEL_FATAL) return "[F] ";
		return null;
	}
}
