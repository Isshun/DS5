package org.smallbox.faraway.game.manager;

import org.smallbox.faraway.game.GameObserver;

/**
 * Created by Alex on 15/06/2015.
 */
public abstract class BaseManager implements GameObserver {
    private boolean _hasBeenInitialized;

    public void update(int tick) {
        onUpdate(tick);
    }
    public void create() {
        _hasBeenInitialized = true;
        onCreate();
    }

    protected abstract void onUpdate(int tick);

    protected void onCreate() {
    }

    @Override
    public boolean hasBeenInitialized() {
        return _hasBeenInitialized;
    }
}
