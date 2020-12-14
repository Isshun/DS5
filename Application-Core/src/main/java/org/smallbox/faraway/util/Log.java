package org.smallbox.faraway.util;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.*;

/**
 * Created by Alex on 13/05/2016.
 */
public class Log {
    private final static boolean EXIT_ON_ERROR = true;
    private final static Logger logger = Logger.getLogger("FarAway");
    private final static Level level = Level.WARNING;
    public static Queue<String> _historyDebug = new ConcurrentLinkedQueue<>();
    public static Queue<String> _history = new ConcurrentLinkedQueue<>();
    public static String _lastErrorMessage;
    public static long _lastErrorTime;
    private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private final ConsoleHandler consoleHandler;

    private static final String[] debugPackages = {
            "org.smallbox.faraway.core.engine",
            "org.smallbox.faraway.module.itemFactory",
            "org.smallbox.faraway.module.consumable"
    };

    private static final String[] infoPackages = {
    };

    {
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

    private static FileOutputStream fos;

    private static void print(Level level, String message) {

        while (_history.size() > 4) {
            _history.poll();
        }
        while (_historyDebug.size() > 4) {
            _historyDebug.poll();
        }
        if (level == Level.FINE) {
            _historyDebug.add(message);
        } else {
            _history.add(message);
        }

//        if (fos == null) {
//            try {
//                fos = new FileOutputStream(new File("W:\\projects\\desktop\\FarAway\\Application\\sysout.log"));
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
//        }

//        try {
//            IOUtils.write(message + "\n", fos);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        if (level == Level.WARNING) {
            System.out.println("[WARNING] " + message);
        }

        if (level == Level.INFO) {
            System.out.println("[INFO] " + message);
        }

        if (level == Level.FINE) {
            System.out.println("[DEBUG] " + message);
        }

    }

    public static void warning(Class cls, String message) {
        print(Level.WARNING, cls.getSimpleName() + ": " + message);
    }

    public static void warning(String message) {
        print(Level.WARNING, message);
    }

    public static void warning(Class cls, String message, Object... objects) {
        print(Level.WARNING, cls.getSimpleName() + ": " + String.format(message, objects));
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
//        Application.exitWithError();
    }

    private static void printError(Throwable t) {
        _lastErrorTime = System.currentTimeMillis();
        _lastErrorMessage = t.getMessage();

        logger.severe(t.getMessage());
//        for (StackTraceElement element : t.getStackTrace()) {
//            logger.severe(element.toString());
//        }
        t.printStackTrace();

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
//        Application.exitWithError();
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
        if (Level.INFO.intValue() >= level.intValue()) {
            if (inPackageList(debugPackages) || inPackageList(infoPackages)) {
                print(Level.INFO, "[" + component + "] " + String.format(message, args));
            }
        }
    }

    public static void info(Class cls, String message, Object... args) {
        if (Level.INFO.intValue() >= level.intValue()) {
            if (inPackageList(debugPackages) || inPackageList(infoPackages)) {
                print(Level.INFO, "[" + cls.getSimpleName() + "] " + String.format(message, args));
            }
        }
    }

    public static void info(String message, Object... args) {
        if (Level.INFO.intValue() >= level.intValue()) {
            if (inPackageList(debugPackages) || inPackageList(infoPackages)) {
                String className = Thread.currentThread().getStackTrace()[2].getClassName();
                print(Level.INFO, "[" + className.substring(className.lastIndexOf('.') + 1) + "] " + String.format(message, args));
            }
        }
    }

    public static void debug(String message, Object... args) {
        if (Level.ALL.intValue() >= level.intValue()) {
            if (inPackageList(debugPackages)) {
                print(Level.FINE, String.format(message, args));
            }
        }
    }

    public static void debug(Class cls, String message, Object... args) {
        if (Level.ALL.intValue() >= level.intValue()) {
            if (inPackageList(debugPackages)) {
                print(Level.FINE, "[" + cls.getSimpleName() + "] " + String.format(message, args));
            }
        }
    }

    public static void verbose(String message, Object... args) {
        debug(message, args);
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

    public void setLevel(String levelName) {
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
