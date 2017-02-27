package org.smallbox.faraway.util;

import org.smallbox.faraway.core.Application;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

/**
 * Created by Alex on 13/05/2016.
 */
public class Log {
    private final static boolean EXIT_ON_ERROR = true;
    private final static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final static Logger logger = Logger.getLogger("FarAway");
    private final static Level level = Level.ALL;

    private static final ConsoleHandler consoleHandler;

    private static final String[] debugPackages = {
            "org.smallbox.faraway.core.engine",
            "org.smallbox.faraway.module.itemFactory",
            "org.smallbox.faraway.module.consumable"
    };

    private static final String[] infoPackages = {
    };

    static {
        logger.setLevel(level);
        consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(level);
        consoleHandler.setFormatter(new SimpleFormatter() {
            @Override
            public String format(LogRecord record) {
                return "[" + record.getSourceMethodName().substring(0, 1).toUpperCase() + "] ["
                        + format.format(new Date(record.getMillis())) + "] "
                        + record.getMessage() + "\n";
            }
        });
        logger.addHandler(consoleHandler);
        logger.setUseParentHandlers(false);
    }

    private static void print(Level level, String message) {
        if (level == Level.INFO) {
            System.out.println(message);
        }

        if (level == Level.FINE) {
            System.out.println(message);
        }
    }

    public static void warning(Class cls, String message) {
        logger.warning(cls.getName() + ": " + message);
    }

    public static void warning(String message) {
        logger.warning(message);
    }

    public static void warning(Class cls, String message, Object... objects) {
        logger.warning(cls.getName() + ": " + String.format(message, objects));
    }

    public static void warning(String message, Object... objects) {
        warning(String.format(message, objects));
    }

    public static void error(Exception e, String message, Object... args) {
        logger.severe(String.format(message, args));
        logger.severe(e.getMessage());
        for (StackTraceElement element : e.getStackTrace()) {
            logger.severe(element.toString());
        }
    }

    public static void error(Throwable t) {
        printError(t);
        Application.exitWithError();
    }

    private static void printError(Throwable t) {
        logger.severe(t.getMessage());
        for (StackTraceElement element : t.getStackTrace()) {
            logger.severe(element.toString());
        }

        if (t.getCause() != null) {
            logger.severe("Cause by:");
            printError(t.getCause());
        }
    }

    public static void error(Class cls, String message) {
        error(cls.getSimpleName() + " - " + message);
    }

    public static void error(String message) {
        logger.severe(message);
        for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
            logger.severe(element.toString());
        }
        Application.exitWithError();
    }

    public static void error(String message, Object... args) {
        error(String.format(message, args));
    }


    public static void fatal(String message) {
        logger.severe(message);
        for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
            logger.severe(element.toString());
        }

        System.exit(1);
    }

    public static void fatal(String message, Object... args) {
        fatal(String.format(message, args));
    }

    public static void error(Exception e, String message) {
        logger.severe(message);
        logger.severe(e.getMessage());
        for (StackTraceElement element : e.getStackTrace()) {
            logger.severe(element.toString());
        }

        if (EXIT_ON_ERROR) {
            System.exit(1);
        }
    }

    public static void info(String component, String message, Object... args) {
        if (inPackageList(debugPackages) || inPackageList(infoPackages)) {
            print(Level.INFO, "[" + component + "] " + String.format(message, args));
        }
    }

    public static void info(Class cls, String message, Object... args) {
        if (inPackageList(debugPackages) || inPackageList(infoPackages)) {
            print(Level.INFO, "[" + cls.getSimpleName() + "] " + String.format(message, args));
        }
    }

    public static void info(String message, Object... args) {
        if (inPackageList(debugPackages) || inPackageList(infoPackages)) {
            String className = Thread.currentThread().getStackTrace()[2].getClassName();
            print(Level.INFO, "[" + className.substring(className.lastIndexOf('.') + 1) + "] " + String.format(message, args));
        }
    }

    public static void debug(String message, Object... args) {
        if (inPackageList(debugPackages)) {
            print(Level.FINE, String.format(message, args));
        }
    }

    public static void verbose(String message, Object... args) {
    }

    private static boolean inPackageList(String[] packageList) {
        String className = Thread.currentThread().getStackTrace()[3].getClassName();

        for (String pkg: packageList) {
            if (className.startsWith(pkg)) {
                return true;
            }
        }

        return true;
    }

    public static void notice(String message) {
        info(message);
    }

    public static void setLevel(String levelName) {
        switch (levelName) {
            case "info":
                logger.setLevel(Level.INFO);
                consoleHandler.setLevel(Level.INFO);
                break;
            case "error":
                logger.setLevel(Level.SEVERE);
                consoleHandler.setLevel(Level.SEVERE);
                break;
            case "warning":
                logger.setLevel(Level.WARNING);
                consoleHandler.setLevel(Level.WARNING);
                break;
            case "debug":
                logger.setLevel(Level.ALL);
                consoleHandler.setLevel(Level.ALL);
                break;
        }
//        if (levelName != null) {
//            logger.setLevel(Level.parse(levelName));
//            consoleHandler.setLevel(Level.parse(levelName));
//        }
    }
}
