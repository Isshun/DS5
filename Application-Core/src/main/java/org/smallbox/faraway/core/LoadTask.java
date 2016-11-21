package org.smallbox.faraway.core;

/**
 * Created by Alex on 29/11/2015.
 */
public abstract class LoadTask implements Runnable {
    public enum State {NONE, RUNNING, COMPLETE}

    public final boolean    onMainThread;
    public final String     label;
    public Throwable        throwable;
    public State            state = State.NONE;

    public LoadTask(String label, boolean onMainThread) {
        this.label = label;
        this.onMainThread = onMainThread;
    }

    @Override
    public void run() {
        try {
            state = State.RUNNING;
            onRun();
            state = State.COMPLETE;
        } catch (Throwable t) {
            throwable = t;
        }
    }

    protected abstract void onRun();
}
