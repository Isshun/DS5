package org.smallbox.faraway.core.engine.module;

import org.smallbox.faraway.GameEvent;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.BindModule;
import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.core.engine.module.java.ModuleManager;
import org.smallbox.faraway.core.game.GameObserver;
import org.smallbox.faraway.core.util.Log;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;

public abstract class ModuleBase implements GameObserver {
    protected final String      TAG = getClass().getSimpleName();

    protected final int         _id = UUID.randomUUID().toString().hashCode();
    protected ModuleInfo        _info;
    protected boolean           _isLoaded;
    protected boolean           _isStarted;

    protected void onLoad() {}
    protected void onUnload() {}
    protected void onCreate() {}

    public final void load() {
        assert !_isLoaded;

        Log.info("[" + _info.name + "] Load");
        onLoad();
        _isLoaded = true;

//        Log.info("[" + _info.name + "] Load");
//        if (runOnMainThread()) {
//            onLoad();
//            _isLoaded = true;
//        } else {
//            Application.moduleManager.getExecutor().execute(() -> {
//                onLoad();
//                _isLoaded = true;
//            });
//        }
    }

    public final void create() {
        Log.info("[" + _info.name + "] createGame");
        onCreate();
    }

    public final void unload() {
        assert _isLoaded;

        Log.info("[" + _info.name + "] Unload");
        if (runOnMainThread()) {
            onUnload();
            _isLoaded = false;
        } else {
            Application.moduleManager.getExecutor().execute(() -> {
                onUnload();
                _isLoaded = false;
            });
        }
    }

    public boolean      runOnMainThread() { return false; }
    public boolean      isThirdParty() { return false; }

    public boolean      isActivate() { return true; }
    public boolean      isLoaded() { return _isLoaded; }
    public boolean      isModuleMandatory() { return false; }

    public int          getModulePriority() { return 0; }
    public ModuleInfo   getInfo() { return _info; }

    public void         setInfo(ModuleInfo info) { _info = info; }

    public boolean onKey(GameEvent event, GameEventListener.Key key) {
        return false;
    }

    public boolean onMouseEvent(GameEventListener.Action action, GameEventListener.MouseButton button, int x, int y) {
        return false;
    }

    protected void printNotice(String message) { Application.notify(observer -> observer.onLog(TAG, message)); }
    protected void printInfo(String message) { Application.notify(observer -> observer.onLog(TAG, message)); }
    protected void printError(String message) { Application.notify(observer -> observer.onLog(TAG, message)); }
    protected void printWarning(String message) { Application.notify(observer -> observer.onLog(TAG, message)); }
    protected void printDebug(String message) { Application.notify(observer -> observer.onLog(TAG, message)); }

    public boolean hasRequiredDependencies(List<? extends ModuleBase> modules) {
        for (Field field: this.getClass().getDeclaredFields()) {
            BindModule bindModule = field.getAnnotation(BindModule.class);
            if (bindModule != null && !checkModuleDependency(modules, field.getType())) {
                return false;
            }
        }
        return true;
    }

    private boolean checkModuleDependency(List<? extends ModuleBase> modules, Class dependencyClass) {
        for (ModuleBase module: modules) {
            if (module.isLoaded() && dependencyClass.isInstance(module)) {
                return true;
            }
        }
        return false;
    }

    public String getName() {
        return _info != null ? _info.name : getClass().getName();
    }
}
