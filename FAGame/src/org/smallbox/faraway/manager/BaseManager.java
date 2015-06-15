package org.smallbox.faraway.manager;

import org.smallbox.faraway.GameObserver;

/**
 * Created by Alex on 15/06/2015.
 */
public abstract class BaseManager implements GameObserver {
    public void update(int tick) {
        onUpdate(tick);
    }

    protected abstract void onUpdate(int tick);
}
