package org.smallbox.faraway.core.task;

import org.smallbox.faraway.core.GameException;

import java.util.function.Supplier;

public abstract class WaitTask extends Task {

    private final Supplier<Float> progressSupplier;

    public WaitTask(String label, boolean onMainThread, Supplier<Float> progressSupplier) {
        super(label, onMainThread);
        this.progressSupplier = progressSupplier;
    }

    public String getLabel() {
        return label + " " + (int)(progressSupplier.get() * 100);
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
