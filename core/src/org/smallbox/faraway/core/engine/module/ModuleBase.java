package org.smallbox.faraway.core.engine.module;

import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameObserver;
import org.smallbox.faraway.core.util.Log;

import java.util.UUID;

/**
 * Created by Alex on 26/11/2015.
 */
public abstract class ModuleBase implements GameObserver {
    protected final String      TAG = getClass().getSimpleName();

    protected final int         _id = UUID.randomUUID().toString().hashCode();
    protected ModuleInfo        _info;
    protected boolean           _isLoaded;
    protected boolean           _isStarted;
    protected boolean           _needCreate;
    protected boolean           _needGameStart;
    protected boolean           _needUpdate;
    protected int               _needUpdateTick;

    public ModuleBase() {
        _isLoaded = loadOnStart();
    }

    protected void onCreate() {}
    protected abstract void onGameStart(Game game);
    protected abstract void onUpdate(int tick);
    protected void onDestroy() {}

    public abstract boolean loadOnStart();

    public boolean      hasOwnThread() { return true; }
    public boolean      isThirdParty() { return false; }
    public boolean      isLoaded() { return true; }

    public int          getModulePriority() { return 0; }
    public ModuleInfo   getInfo() { return _info; }

    public void         setInfo(ModuleInfo info) { _info = info; }

    public boolean onKey(GameEventListener.Key key) {
        return false;
    }

    public boolean onMouseEvent(GameEventListener.Action action, GameEventListener.MouseButton button, int x, int y) {
        return false;
    }

    public void create() {
        Log.info("Create java module: " + _info.name);
        onCreate();
        if (hasOwnThread()) {
//            _needCreate = true;
            new Thread(() -> {
                try {
                    while (Application.getInstance().isRunning()) {
                        if (_needCreate) {
                            _needCreate = false;
                            onCreate();
                        }
                        if (_needGameStart) {
                            _needGameStart = false;
                            onGameStart(Game.getInstance());
                            _isStarted = true;
                        }
                        if (_needUpdate) {
                            _needUpdate = false;
                            onUpdate(_needUpdateTick);
                        }
                        Thread.sleep(16);
                    }
                } catch (Error | Exception e) {
                    e.printStackTrace();
                    Application.getInstance().setRunning(false);
                }
            }).start();
        } else {
//            onCreate();
        }
        _isLoaded = true;
    }

    public void startGame(Game game) {
        Log.debug("Start java module: " + _info.name);
        if (hasOwnThread()) {
            _needGameStart = true;
        } else {
            onGameStart(game);
            _isStarted = true;
        }
    }

    public void update(int tick) {
    }

    public void destroy() {
        onDestroy();
        _isLoaded = false;
    }

    public boolean isModuleMandatory() {
        return false;
    }

    protected void printNotice(String message) { Application.getInstance().notify(observer -> observer.onLog(TAG, message)); }
    protected void printInfo(String message) { Application.getInstance().notify(observer -> observer.onLog(TAG, message)); }
    protected void printError(String message) { Application.getInstance().notify(observer -> observer.onLog(TAG, message)); }
    protected void printWarning(String message) { Application.getInstance().notify(observer -> observer.onLog(TAG, message)); }
    protected void printDebug(String message) { Application.getInstance().notify(observer -> observer.onLog(TAG, message)); }
}
