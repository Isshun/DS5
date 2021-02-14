package org.smallbox.faraway.core.module;

import org.smallbox.faraway.core.game.GameObserver;
import org.smallbox.faraway.util.log.Log;

public abstract class ModuleBase implements GameObserver {
    protected ModuleInfo        _info;
    protected boolean           _isLoaded;

    public final void load() {
        assert !_isLoaded;

        Log.debug(getClass(), "Load module");

        _isLoaded = true;
    }

    public boolean      isActivate() { return true; }
    public boolean      isLoaded() { return _isLoaded; }

    public int          getModulePriority() { return 0; }
    public ModuleInfo   getInfo() { return _info; }

    public void         setInfo(ModuleInfo info) { _info = info; }

    public String getName() {
        return _info != null ? _info.name : getClass().getSimpleName();
    }
}
