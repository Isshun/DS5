package org.smallbox.faraway.core.task;

import org.smallbox.faraway.client.ProgressCallback;
import org.smallbox.faraway.util.GameException;

import java.util.Optional;

public abstract class WaitTask extends Task {

    private final ProgressCallback progressSupplier;

    public WaitTask(String label, boolean onMainThread, ProgressCallback progressSupplier) {
        super(label, onMainThread);
        this.progressSupplier = progressSupplier;
    }

    public String getLabel() {
        return label + Optional.ofNullable(progressSupplier).map(callback -> " " + callback.getCurrent() + "/" + callback.getTotal()).orElse("");
//        return label + " " + (int)(progressSupplier.getProgress() * 100);
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
