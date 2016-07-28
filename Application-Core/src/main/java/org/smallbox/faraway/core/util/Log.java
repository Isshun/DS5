package org.smallbox.faraway.core.util;

import org.apache.commons.lang3.builder.RecursiveToStringStyle;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.logging.*;

/**
 * Created by Alex on 13/05/2016.
 */
public class Log {
    private final static boolean EXIT_ON_ERROR = true;
    private final static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final static Logger logger = Logger.getLogger("FarAway");

    static {
        logger.setLevel(Level.INFO);
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new SimpleFormatter() {
            @Override
            public String format(LogRecord record) {
                return "[" + record.getSourceMethodName() + "] ["
                        + format.format(new Date(record.getMillis())) + "] "
                        + record.getMessage() + "\n";
            }
        });
        logger.getHandlers();
        logger.addHandler(consoleHandler);
        logger.setUseParentHandlers(false);
    }

    public static void warning(String message) {
        logger.warning(message);
    }

    public static void error(Exception e, String message, Object... args) {
        logger.severe(String.format(message, args));
        logger.severe(e.getMessage());
        for (StackTraceElement element: e.getStackTrace()) {
            logger.severe(element.toString());
        }
    }

    public static void error(Throwable t) {
        logger.severe(t.getMessage());
        for (StackTraceElement element: t.getStackTrace()) {
            logger.severe(element.toString());
        }

        if (t.getCause() != null) {
            logger.severe("Cause by:");
            error(t.getCause());
        }
    }

    public static void error(String message) {
        logger.severe(message);
        for (StackTraceElement element: Thread.currentThread().getStackTrace()) {
            logger.severe(element.toString());
        }

        if (EXIT_ON_ERROR) {
            System.exit(1);
        }
    }

    public static void error(String message, Object... args) {
        error(String.format(message, args));
    }


    public static void fatal(String message) {
        logger.severe(message);
        for (StackTraceElement element: Thread.currentThread().getStackTrace()) {
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
        for (StackTraceElement element: e.getStackTrace()) {
            logger.severe(element.toString());
        }

        if (EXIT_ON_ERROR) {
            System.exit(1);
        }
    }

    public static void info(String message) {
        logger.log(Level.INFO, message);
    }

    public static void info(String message, Object... args) {
        info(String.format(message, args));
    }

    public static void debug(String message) {
        logger.fine(message);
    }

    public static void debug(String message, Object... args) {
        logger.fine(String.format(message, args));
    }

    public static void notice(String message) {
        info(message);
    }

    public static void dump(Object object) {
        System.out.println(ReflectionToStringBuilder.toString(object, RecursiveToStringStyle.MULTI_LINE_STYLE));
    }
}
