package org.smallbox.faraway.util.transition;

import java.time.LocalDateTime;

import static java.time.temporal.ChronoUnit.SECONDS;

public abstract class Transition<T> {
    protected final LocalDateTime fromDateTime;
    protected final LocalDateTime toDateTime;
    protected final T startValue;
    protected final T endValue;

    public Transition(T startValue, T endValue, LocalDateTime fromDateTime, LocalDateTime toDateTime) {
        this.fromDateTime = fromDateTime;
        this.toDateTime = toDateTime;
        this.startValue = startValue != null ? startValue : endValue;
        this.endValue = endValue;
    }

    public T getStartValue() {
        return this.startValue;
    }

    public int getValue(LocalDateTime currentDateTime) {
        float progress = Math.min(1, fromDateTime.until(currentDateTime, SECONDS) / (float) fromDateTime.until(toDateTime, SECONDS));
        return onGetValue(progress);
    }

    public abstract int onGetValue(float progress);
}
