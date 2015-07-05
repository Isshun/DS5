package org.smallbox.faraway.game.manager;

import org.smallbox.faraway.game.GameObserver;
import org.smallbox.faraway.util.Log;

/**
 * Created by Alex on 15/06/2015.
 */
public abstract class BaseManager implements GameObserver {
    private int     _nbUpdate;
    private long    _totalTime;
    protected int   _updateInterval = 1;

    public void update(int tick) {
        if (tick % _updateInterval == 0) {
            long time = System.currentTimeMillis();
            onUpdate(tick);
            _totalTime += (System.currentTimeMillis() - time);
            _nbUpdate++;
        }
    }
    public void create() {
        onCreate();
    }

    protected abstract void onUpdate(int tick);

    protected void onCreate() {
    }

    public void dump() {
        if (_nbUpdate != 0) {
            Log.notice("Manager: " + this.getClass().getSimpleName() + ",\tupdate: " + _nbUpdate + ",\tavg time: " + _totalTime / _nbUpdate);
        }
    }
}
