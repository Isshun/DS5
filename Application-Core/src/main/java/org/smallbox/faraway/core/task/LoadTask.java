package org.smallbox.faraway.core.task;

import org.smallbox.faraway.util.GameException;

public abstract class LoadTask extends Task {

    public LoadTask(String label, boolean onMainThread) {
        super(label, onMainThread);
    }

    @Override
    public boolean run() {
        if (state == State.RUNNING || state == State.RUNNING_BACKGROUND) {
            try {
                onRun();
            } catch (Throwable t) {
                t.printStackTrace();
                throwable = t;
            }
        } else {
            throw new GameException(LoadTask.class, "Only task with JOB_RUNNING status can be run");
        }
        return true;
    }

    protected abstract void onRun();
}
