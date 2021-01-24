package org.smallbox.faraway;

import org.smallbox.faraway.util.UUIDUtils;

public abstract class GameTask {
    public final long id;
    public final String name;
    public final String label;
    public final long duration;
    public long elapsed;

    public GameTask(String name, String label, long duration) {
        this.name = name;
        this.id = UUIDUtils.getUUID();
        this.label = label;
        this.duration = duration;
    }

    public boolean isComplete() {
        return elapsed >= duration;
    }

    public void update() {

    }

    public abstract void onStart();
    public abstract void onUpdate();
    public abstract void onClose();
}
