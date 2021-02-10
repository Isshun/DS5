package org.smallbox.faraway.util.transition;

import java.time.LocalDateTime;

import static java.time.temporal.ChronoUnit.SECONDS;

public abstract class Transition<T> {
    protected LocalDateTime fromDateTime;
    protected LocalDateTime toDateTime;
    protected final T startValue;
    protected final T endValue;

    public Transition(T startValue, T endValue) {
        this.startValue = startValue != null ? startValue : endValue;
        this.endValue = endValue;
    }

    public T getValue(float progress) {
        return onGetValue(progress);
    }

    public T getValue(LocalDateTime currentDateTime) {
        return onGetValue(Math.min(1, fromDateTime.until(currentDateTime, SECONDS) / (float) fromDateTime.until(toDateTime, SECONDS)));
    }

    protected abstract T onGetValue(float progress);

    public void setInterval(LocalDateTime fromDateTime, LocalDateTime toDateTime) {
        this.fromDateTime = fromDateTime;
        this.toDateTime = toDateTime;
    }
}
