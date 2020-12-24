package org.smallbox.faraway.core.task;

import org.smallbox.faraway.core.GameException;

public abstract class LoadTask implements Task {
    public enum State {NONE, WAITING, RUNNING, COMPLETE}

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
        if (state == State.RUNNING) {
            try {
                onRun();
            } catch (Throwable t) {
                t.printStackTrace();
                throwable = t;
            }
        } else {
            throw new GameException(LoadTask.class, "Only task with JOB_RUNNING status can be run");
        }
    }

    protected abstract void onRun();
}
