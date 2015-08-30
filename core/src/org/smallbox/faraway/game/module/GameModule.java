package org.smallbox.faraway.game.module;

import org.smallbox.faraway.data.serializer.SerializerInterface;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.GameObserver;
import org.smallbox.faraway.util.Log;

/**
 * Created by Alex on 15/06/2015.
 */
public abstract class GameModule implements GameObserver {
    private final String TAG = getClass().getSimpleName();

    private int         _nbUpdate;
    private long        _totalTime;
    protected int       _updateInterval = 1;
    private boolean     _isLoaded;

    public void update(int tick) {
        if (tick % _updateInterval == 0) {
            long time = System.currentTimeMillis();
            onUpdate(tick);
            _totalTime += (System.currentTimeMillis() - time);
            _nbUpdate++;
        }
    }

    protected abstract void onUpdate(int tick);

    public GameModule() {
        _isLoaded = loadOnStart();
    }

    public void create() {
        onCreate();
        _isLoaded = true;
    }

    protected void onCreate() {}

    public void dump() {
        if (_nbUpdate != 0) {
            printNotice("Manager: " + this.getClass().getSimpleName() + ",\tupdate: " + _nbUpdate + ",\tavg time: " + _totalTime / _nbUpdate);
        }
    }

    protected void printNotice(String message) { Game.getInstance().notify(observer -> observer.onLog(TAG, message)); }
    protected void printInfo(String message) { Game.getInstance().notify(observer -> observer.onLog(TAG, message)); }
    protected void printError(String message) { Game.getInstance().notify(observer -> observer.onLog(TAG, message)); }
    protected void printWarning(String message) { Game.getInstance().notify(observer -> observer.onLog(TAG, message)); }
    protected void printDebug(String message) { Game.getInstance().notify(observer -> observer.onLog(TAG, message)); }

    public SerializerInterface getSerializer() {
        return null;
    }

    protected boolean loadOnStart() {return true; }

    public boolean isLoaded() {
        return _isLoaded;
    }

    public void destroy() {
        onDestroy();
        _isLoaded = false;
    }

    protected void onDestroy() {
    }
}
