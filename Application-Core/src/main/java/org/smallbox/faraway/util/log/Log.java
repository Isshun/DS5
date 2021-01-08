package org.smallbox.faraway.util.log;

import org.smallbox.faraway.core.dependencyInjector.DependencyManager;
import org.smallbox.faraway.core.game.service.applicationConfig.ApplicationConfig;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;

public class Log {
    private final static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final static LogLevel level = LogLevel.INFO;
    public static Queue<String> _historyDebug = new ConcurrentLinkedQueue<>();
    public static Queue<String> _history = new ConcurrentLinkedQueue<>();
    public static String _lastErrorMessage;
    public static long _lastErrorTime;

    private static final String[] debugPackages = {
            "org.smallbox.faraway.core.engine",
            "org.smallbox.faraway.module.itemFactory",
            "org.smallbox.faraway.module.consumable"
    };

    private static final String[] infoPackages = {
    };

    private static void print(LogLevel level, String message) {
        String date = LocalDateTime.now().format(dateTimeFormatter);

        ApplicationConfig applicationConfig = DependencyManager.getInstance().getDependency(ApplicationConfig.class);
        if (Objects.nonNull(applicationConfig)) {
            while (_history.size() > applicationConfig.debug.logLineNumber) {
                _history.poll();
            }
            while (_historyDebug.size() > applicationConfig.debug.logLineNumber) {
                _historyDebug.poll();
            }
            if (level == LogLevel.DEBUG) {
                _historyDebug.add(message);
            } else {
                _history.add(date + " [" + level.name() + "] " + message);
            }
        }

        System.out.println(date + " [" + level.name() + "] " + message);
    }

    public static void warning(Class cls, String message) {
        print(LogLevel.WARNING, cls.getSimpleName() + ": " + message);
    }

    public static void warning(String message) {
        print(LogLevel.WARNING, message);
    }

    public static void warning(Class cls, String message, Object... objects) {
        print(LogLevel.WARNING, cls.getSimpleName() + ": " + String.format(message, objects));
    }

    public static void warning(String message, Object... objects) {
        warning(String.format(message, objects));
    }

    public static void error(Throwable throwable, String message, Object... args) {
        error(throwable, String.format(message, args));
    }

    public static void error(Throwable throwable) {
        error(throwable, null);
    }

    public static void error(Class<?> cls, String message) {
        error(cls.getSimpleName() + " - " + message);
    }

    public static void error(String message) {
        error((Throwable) null, message);
    }

    public static void error(String message, Object... args) {
        error((Throwable) null, String.format(message, args));
    }

    public static void error(Throwable throwable, String message) {

        if (message != null) {
            print(LogLevel.ERROR, message);
        }

        if (throwable != null) {

            if (throwable.getMessage() != null) {
                print(LogLevel.ERROR, throwable.getClass().getSimpleName() + ": " + throwable.getMessage());
            } else {
                print(LogLevel.ERROR, throwable.getClass().getSimpleName());
            }

            for (StackTraceElement element : throwable.getStackTrace()) {
                print(LogLevel.ERROR, "  " + element.toString());
            }

            if (throwable.getCause() != null) {
                error(throwable.getCause(), "Caused by");
            }

        }

//        for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
//            print(LogLevel.ERROR, element.toString());
//        }
    }

    public static void info(String component, String message, Object... args) {
        if (LogLevel.INFO.ordinal() >= level.ordinal()) {
            if (inPackageList(debugPackages) || inPackageList(infoPackages)) {
                print(LogLevel.INFO, "[" + component + "] " + String.format(message, args));
            }
        }
    }

    public static void info(Class cls, String message, Object... args) {
        if (LogLevel.INFO.ordinal() >= level.ordinal()) {
            if (inPackageList(debugPackages) || inPackageList(infoPackages)) {
                print(LogLevel.INFO, "[" + cls.getSimpleName() + "] " + String.format(message, args));
            }
        }
    }

    public static void info(String message, Object... args) {
        if (LogLevel.INFO.ordinal() >= level.ordinal()) {
            if (inPackageList(debugPackages) || inPackageList(infoPackages)) {
                String className = Thread.currentThread().getStackTrace()[2].getClassName();
                print(LogLevel.INFO, "[" + className.substring(className.lastIndexOf('.') + 1) + "] " + String.format(message, args));
            }
        }
    }

    public static void command(String message) {
        print(LogLevel.ERROR, message);
    }

    public static void debug(String message, Object... args) {
        if (Level.ALL.intValue() >= level.ordinal()) {
            if (inPackageList(debugPackages)) {
                print(LogLevel.DEBUG, String.format(message, args));
            }
        }
    }

    public static void debug(Class cls, String message, Object... args) {
        if (Level.ALL.intValue() >= level.ordinal()) {
            if (inPackageList(debugPackages)) {
                print(LogLevel.DEBUG, "[" + cls.getSimpleName() + "] " + String.format(message, args));
            }
        }
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

}
