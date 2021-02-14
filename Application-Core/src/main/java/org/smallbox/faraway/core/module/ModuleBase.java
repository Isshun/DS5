package org.smallbox.faraway.core.module;

import org.smallbox.faraway.core.game.GameObserver;
import org.smallbox.faraway.util.log.Log;

public abstract class ModuleBase implements GameObserver {
    protected ModuleInfo        _info;

    public final void load() {
        Log.debug(getClass(), "Load module");
    }

    public ModuleInfo   getInfo() { return _info; }

    public void         setInfo(ModuleInfo info) { _info = info; }

    public String getName() {
        return _info != null ? _info.name : getClass().getSimpleName();
    }
}
