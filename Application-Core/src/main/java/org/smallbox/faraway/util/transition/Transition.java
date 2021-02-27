package org.smallbox.faraway.util.transition;

import com.badlogic.gdx.math.Interpolation;

import java.time.LocalDateTime;

import static java.time.temporal.ChronoUnit.SECONDS;

public abstract class Transition<T> {
    public enum Repeat {NONE, BOUNCE, RESTART}
    protected enum Mode {DURATION, INTERVAL}
    protected Repeat repeat = Repeat.NONE;
    protected Mode mode;
    protected LocalDateTime fromDateTime;
    protected LocalDateTime toDateTime;
    protected Interpolation interpolation;
    protected boolean rewind;
    protected long fromTime;
    protected long duration;
    protected final T startValue;
    protected final T endValue;

    public Transition(T startValue, T endValue) {
        this.startValue = startValue != null ? startValue : endValue;
        this.endValue = endValue;
    }

    public T getValue(float progress) {
        return doGetValue(progress);
    }

    public T getValue(LocalDateTime currentDateTime) {
        return doGetValue(Math.min(1, fromDateTime.until(currentDateTime, SECONDS) / (float) fromDateTime.until(toDateTime, SECONDS)));
    }

    public T getValue() {
        return doGetValue(Math.min(1, (System.currentTimeMillis() - fromTime) / (float)duration));
    }

    private T doGetValue(float progress) {

        if (rewind) {
            progress = 1 - progress;
        }

        if (interpolation != null) {
            progress = interpolation.apply(progress);
        }

        if (mode == Mode.DURATION && fromTime + duration <= System.currentTimeMillis()) {
            if (repeat == Repeat.RESTART) {
                fromTime = System.currentTimeMillis();
            }
            if (repeat == Repeat.BOUNCE) {
                fromTime = System.currentTimeMillis();
                rewind = !rewind;
            }
        }

        return onGetValue(progress);
    }

    protected abstract T onGetValue(float progress);

    public void setInterval(LocalDateTime fromDateTime, LocalDateTime toDateTime) {
        this.mode = Mode.INTERVAL;
        this.fromDateTime = fromDateTime;
        this.toDateTime = toDateTime;
    }

    public void setDuration(long duration) {
        this.mode = Mode.DURATION;
        this.fromTime = System.currentTimeMillis();
        this.duration = duration;
    }

    public void setInterpolation(Interpolation interpolation) {
        this.interpolation = interpolation;
    }

    public void setRepeat(Repeat repeat) {
        this.repeat = repeat;
    }
}
