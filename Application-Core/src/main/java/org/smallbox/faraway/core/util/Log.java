package org.smallbox.faraway.core.util;

import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.game.Game;

public class Log {
    public static int LEVEL_NOTICE = 0;
    public static int LEVEL_FATAL = 1;
    public static int LEVEL_ERROR = 2;
    public static int LEVEL_WARNING = 3;
    public static int LEVEL_INFO = 4;
    public static int LEVEL_DEBUG = 5;
    public static int LEVEL = LEVEL_DEBUG;

    public static void debug(String str) {
        if (LEVEL < LEVEL_DEBUG) return;
        println(LEVEL_DEBUG, str);
    }

    public static void info(String str, Object... args) {
        if (LEVEL < LEVEL_INFO) return;
        println(LEVEL_INFO, str, args);
    }

    public static void warning(String str) {
        if (LEVEL < LEVEL_WARNING) return;
        println(LEVEL_WARNING, str);
    }

    public static void error(String str) {
        if (LEVEL < LEVEL_ERROR) return;
        println(LEVEL_ERROR, str);

        // Print stack
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        Log.debug("Error occurred \"" + str + "\"");
        for (int i = 2; i < elements.length; i++) {
            Log.debug("          " + elements[i].toString());
        }
    }

    public static void notice(String str) {
        if (LEVEL < LEVEL_NOTICE) return;
        println(LEVEL_NOTICE, str);
    }

    private static void println(int level, String str, Object... args) {
        if (str != null) {
//            if (Game.getInstance() != null) {
//                Application.getInstance().notify(observer -> observer.onLog("System", str));
//            }

            System.out.print(String.valueOf(System.currentTimeMillis() / 1000));
            System.out.print(" ");
            System.out.print(getPrefix(level));
            System.out.println(String.format(str, args));
//
//            if (UserInterface.getInstance() != null) {
//                UserInterface.getInstance().addMessage(level, str);
//            }
        }
    }

    public static String getPrefix(int level) {
        if (level == LEVEL_DEBUG)     return "[D] ";
        if (level == LEVEL_INFO)     return "[I] ";
        if (level == LEVEL_WARNING) return "[W] ";
        if (level == LEVEL_ERROR)     return "[E] ";
        if (level == LEVEL_FATAL)     return "[F] ";
        if (level == LEVEL_NOTICE)     return "[N] ";
        return null;
    }
}
