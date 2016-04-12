package org.smallbox.faraway.core.engine.module;

import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.core.engine.module.java.ModuleManager;
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

    public ModuleBase() {
        _isLoaded = loadOnStart();
    }

    protected void onGameInit() {}
    protected void onGameStart(Game game) {}
    protected void onGameUpdate(int tick) {}
    protected void onDestroy() {}

    public abstract boolean loadOnStart();

    public boolean      runOnMainThread() { return false; }
    public boolean      isThirdParty() { return false; }
    public boolean      isLoaded() { return true; }
    public boolean      isModuleMandatory() { return false; }

    public int          getModulePriority() { return 0; }
    public ModuleInfo   getInfo() { return _info; }

    public void         setInfo(ModuleInfo info) { _info = info; }

    public boolean onKey(GameEventListener.Key key) {
        return false;
    }

    public boolean onMouseEvent(GameEventListener.Action action, GameEventListener.MouseButton button, int x, int y) {
        return false;
    }

    // TODO: Only on GameModule
    public void initGame() {
        Log.info("Init java module: " + _info.name);
        onGameInit();
        if (runOnMainThread()) {
            onGameInit();
            _isLoaded = true;
        } else {
            ModuleManager.getInstance().getExecutor().execute(() -> {
                onGameInit();
                _isLoaded = true;
            });
        }
    }

    // TODO: Only on GameModule
    public void startGame(Game game) {
        Log.debug("Start java module: " + _info.name);
        if (runOnMainThread()) {
            onGameStart(game);
            _isStarted = true;
        } else {
            ModuleManager.getInstance().getExecutor().execute(() -> {
                onGameStart(game);
                _isStarted = true;
            });
        }
    }

    // TODO: Only on GameModule
    public void updateGame(int tick) {
    }

    public void destroy() {
        onDestroy();
        _isLoaded = false;
    }

    protected void printNotice(String message) { Application.getInstance().notify(observer -> observer.onLog(TAG, message)); }
    protected void printInfo(String message) { Application.getInstance().notify(observer -> observer.onLog(TAG, message)); }
    protected void printError(String message) { Application.getInstance().notify(observer -> observer.onLog(TAG, message)); }
    protected void printWarning(String message) { Application.getInstance().notify(observer -> observer.onLog(TAG, message)); }
    protected void printDebug(String message) { Application.getInstance().notify(observer -> observer.onLog(TAG, message)); }
}
