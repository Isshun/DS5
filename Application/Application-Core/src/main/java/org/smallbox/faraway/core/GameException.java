package org.smallbox.faraway.core;

/**
 * Created by Alex on 04/03/2017.
 */
public class GameException extends RuntimeException {

    public GameException(Class cls, Exception e) {
        super(cls.getSimpleName(), e);
    }

    public GameException(Class cls, String message, Object... args) {
        super(formatMessage(cls, message, args));
    }

    public GameException(Class cls, Exception e, String message) {
        super(cls.getSimpleName() + " " + message, e);
    }

    private static String formatMessage(Class cls, String message, Object... args) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n").append("Class: ").append(cls.getSimpleName());
        stringBuilder.append("\n").append("Message: ").append(message);
        stringBuilder.append("\n").append("Values: ");
        for (Object arg: args) {
            stringBuilder.append("\n").append("  - ").append(arg.getClass().getSimpleName()).append(" = ").append(String.valueOf(arg));
        }
        stringBuilder.append("\n").append("Stack: ");
        return stringBuilder.toString();
    }
}