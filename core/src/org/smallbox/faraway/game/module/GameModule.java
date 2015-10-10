package org.smallbox.faraway.game.module;

import org.smallbox.faraway.module.ModuleInfo;
import org.smallbox.faraway.data.serializer.SerializerInterface;
import org.smallbox.faraway.engine.GameEventListener;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.GameObserver;
import org.smallbox.faraway.game.model.ObjectModel;
import org.smallbox.faraway.game.model.character.base.CharacterModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 15/06/2015.
 */
public abstract class GameModule extends ObjectModel implements GameObserver {
    private final String TAG = getClass().getSimpleName();
    private final ModuleInfo _info;
    private List<EventListener> _listeners;
    private boolean _isActivate;

    public abstract class EventListener<T> {
        public abstract void onEvent(T data);
    }

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

    public GameModule() {
        _isLoaded = loadOnStart();
        _info = new ModuleInfo();
        _info.name = TAG;
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

    protected void printNotice(String message) {
        Game.getInstance().notify(observer -> observer.onLog(TAG, message));
    }
    protected void printInfo(String message) {
        Game.getInstance().notify(observer -> observer.onLog(TAG, message));
    }
    protected void printError(String message) {
        Game.getInstance().notify(observer -> observer.onLog(TAG, message));
    }
    protected void printWarning(String message) {
        Game.getInstance().notify(observer -> observer.onLog(TAG, message));
    }
    protected void printDebug(String message) {
        Game.getInstance().notify(observer -> observer.onLog(TAG, message));
    }

    public SerializerInterface getSerializer() {
        return null;
    }

    protected boolean loadOnStart() {
        return true;
    }

    public boolean isLoaded() {
        return _isLoaded;
    }

    public boolean isMandatory() {
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

    public int getPriority() {
        return 0;
    }

    public boolean isThirdParty() {
        return false;
    }

    public void setActivate(boolean isActivate) {
        _isActivate = isActivate;
    }

    public boolean isActivate() {
        return _isActivate;
    }

    public ModuleInfo getInfo() {
        return _info;
    }

}