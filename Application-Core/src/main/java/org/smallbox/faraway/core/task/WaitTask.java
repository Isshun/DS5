package org.smallbox.faraway.core.task;

import org.smallbox.faraway.core.GameException;

public abstract class WaitTask extends Task {

    public WaitTask(String label, boolean onMainThread) {
        super(label, onMainThread);
    }

    @Override
    public boolean run() {
        if (state == State.RUNNING || state == State.BLOCKING) {
            try {
                return onRun();
            } catch (Throwable t) {
                t.printStackTrace();
                throwable = t;
            }
        } else {
            throw new GameException(WaitTask.class, "Only task with state RUNNING or BLOCKING status can be run");
        }
        return false;
    }

    protected abstract boolean onRun();
}
