package org.smallbox.faraway.core;

public class GameException extends RuntimeException {

    public GameException(Class cls, Throwable throwable) {
        super(cls.getSimpleName(), throwable);
    }

    public GameException(Class cls, String message, Object... args) {
        super(formatMessage(cls, message, args));
    }

    public GameException(Class cls, Throwable throwable, String message) {
        super(cls.getSimpleName() + " " + message, throwable);
    }

    private static String formatMessage(Class cls, String message, Object... args) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n").append("Class: ").append(cls.getSimpleName());
        stringBuilder.append("\n").append("Message: ").append(message);
        stringBuilder.append("\n").append("Values: ");
        for (Object arg: args) {
            stringBuilder.append("\n").append("  - ").append(arg.getClass().getSimpleName()).append(" = ").append(arg);
        }
        stringBuilder.append("\n").append("Stack: ");
        return stringBuilder.toString();
    }
}
