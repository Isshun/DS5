package org.smallbox.faraway.core;

public class ApplicationException extends Exception {
    private final Exception originalException;
    private final String globalMessage;

    public ApplicationException(Exception originalException, String globalMessage) {
        this.originalException = originalException;
        this.globalMessage = globalMessage;
    }

    @Override
    public String getMessage() {
        return globalMessage;
    }

    @Override
    public Throwable getCause() {
        return originalException;
    }

}
