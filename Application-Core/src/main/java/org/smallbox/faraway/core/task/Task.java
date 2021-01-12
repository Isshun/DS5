package org.smallbox.faraway.core.task;

public abstract class Task {
    public final String     label;
    public final boolean    onMainThread;
    public Throwable        throwable;
    public State            state = State.NONE;

    public Task(String label, boolean onMainThread) {
        this.onMainThread = onMainThread;
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public abstract boolean run();
}
