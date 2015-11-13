package org.smallbox.faraway.core.module;

import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.data.serializer.SerializerInterface;
import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameObserver;
import org.smallbox.faraway.core.game.model.ObjectModel;
import org.smallbox.faraway.core.game.module.character.model.base.CharacterModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 15/06/2015.
 */
public abstract class GameModule extends ObjectModel implements GameObserver {
    protected final String      TAG = getClass().getSimpleName();

    protected ModuleInfo        _info;
    private int                 _nbUpdate;
    private long                _totalTime;
    protected int               _updateInterval = 1;
    private boolean             _isLoaded;
    private List<Long>          _updateTimeHistory = new ArrayList<>();
    private long                _updateTime;
    private boolean             _needCreate;
    private boolean             _needLoad;
    private boolean             _needUpdate;
    private int                 _needUpdateTick;

    public GameModule() {
        _isLoaded = loadOnStart();
    }

    public void update(int tick) {
        if (hasOwnThread()) {
            _needUpdate = true;
            _needUpdateTick = tick;
        } else {
            innerUpdate(tick);
        }
    }

    private void innerUpdate(int tick) {
        if (tick % _updateInterval == 0) {
            long time = System.currentTimeMillis();
            onUpdate(tick);
            _updateTimeHistory.add((System.currentTimeMillis() - time));
            if (_updateTimeHistory.size() > 10) {
                _updateTimeHistory.remove(0);
            }
            _updateTime = 0;
            for (long t: _updateTimeHistory) {
                _updateTime += t;
            }
            _updateTime = _updateTime / _updateTimeHistory.size();
            _totalTime += (System.currentTimeMillis() - time);
            _nbUpdate++;
        }
    }

    public void create() {
        System.out.println("Create java module: " + _info.name);
        if (hasOwnThread()) {
            _needCreate = true;
            new Thread(() -> {
                try {
                    while (Application.getInstance().isRunning()) {
                        if (_needCreate) {
                            _needCreate = false;
                            onCreate();
                        }
                        if (_needLoad) {
                            _needLoad = false;
                            onLoaded(Game.getInstance());
                        }
                        if (_needUpdate) {
                            _needUpdate = false;
                            onUpdate(_needUpdateTick);
                        }
                        try {
                            Thread.sleep(16);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Application.getInstance().setRunning(false);
                }
            }).start();
        } else {
            onCreate();
        }
        _isLoaded = true;
    }

    public void load(Game game) {
        System.out.println("Load java module: " + _info.name);
        if (hasOwnThread()) {
            _needLoad = true;
        } else {
            onLoaded(game);
        }
        _isLoaded = true;
    }

    public void destroy() {
        onDestroy();
        _isLoaded = false;
    }

    protected void onCreate() {}
    protected abstract void onLoaded(Game game);
    protected abstract void onUpdate(int tick);
    protected void onDestroy() {}

    public void dump() {
        if (_nbUpdate != 0) {
            printNotice("Manager: " + this.getClass().getSimpleName() + ",\tupdate: " + _nbUpdate + ",\tavg time: " + _totalTime / _nbUpdate);
        }
    }

    protected void printNotice(String message) { Application.getInstance().notify(observer -> observer.onLog(TAG, message)); }
    protected void printInfo(String message) { Application.getInstance().notify(observer -> observer.onLog(TAG, message)); }
    protected void printError(String message) { Application.getInstance().notify(observer -> observer.onLog(TAG, message)); }
    protected void printWarning(String message) { Application.getInstance().notify(observer -> observer.onLog(TAG, message)); }
    protected void printDebug(String message) { Application.getInstance().notify(observer -> observer.onLog(TAG, message)); }

    public SerializerInterface getSerializer() {
        return null;
    }

    protected boolean loadOnStart() {
        return true;
    }

    public boolean isModuleMandatory() {
        return false;
    }

    public boolean onKey(GameEventListener.Key key) {
        return false;
    }

    public boolean onMouseEvent(GameEventListener.Action action, GameEventListener.MouseButton button, int x, int y) {
        return false;
    }

    public long         getModuleUpdateTime() { return _updateTime; }
    public int          getModulePriority() {
        return 0;
    }
    public ModuleInfo   getInfo() { return _info; }

    public void         setInfo(ModuleInfo info) { _info = info; }

    public boolean      isThirdParty() {
        return false;
    }
    public boolean      isLoaded() { return _isLoaded; }
    public boolean      hasOwnThread() { return false; }

    public void         onUpdateDo() {}
}