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
    public abstract class EventListener<T> {
        public abstract void onEvent(T data);
    }

    protected final String      TAG = getClass().getSimpleName();

    protected ModuleInfo        _info;
    private List<EventListener> _listeners;
    private int                 _nbUpdate;
    private long                _totalTime;
    protected int               _updateInterval = 1;
    private boolean             _isLoaded;
    private List<Long>          _updateTimeHistory = new ArrayList<>();
    private long                _updateTime;

    public GameModule() {
        _isLoaded = loadOnStart();
    }

    public void update(int tick) {
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

    protected void addEventListener(String tag, EventListener<CharacterModel> listener) {
        Game.getInstance().addEventListener(listener);
        _listeners.add(listener);
    }

    public void create() {
        System.out.println("Load java module: " + _info.name);

        _listeners = new ArrayList<>();
        onLoaded();
        _isLoaded = true;
    }

    public void destroy() {
        onDestroy();
        _isLoaded = false;
    }

    protected void onDestroy() {
        _listeners.forEach(Game.getInstance()::removeEventListener);
    }

    protected abstract void onLoaded();
    protected abstract void onUpdate(int tick);

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

    public void refresh(int update) {
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

    public void         onUpdateDo() {}
}