package org.smallbox.faraway.core;

/**
 * Created by Alex on 29/11/2015.
 */
public abstract class LoadTask {
    public final Runnable   runnable;
    public final boolean    onMainThread;
    public final String     message;
    public String           messageDetail;

    public LoadTask(GDXApplication application, String message) {
        this.message = message;
        this.runnable = () -> {
            onExecute();
            application.onTaskComplete();
        };
        this.onMainThread = false;
    }

    public LoadTask(GDXApplication application, String message, boolean onMainThread) {
        this.message = message;
        this.runnable = () -> {
            onExecute();
            application.onTaskComplete();
        };
        this.onMainThread = onMainThread;
    }

    public abstract void onExecute();
}
