package org.smallbox.faraway.core.module.lua;

import org.smallbox.faraway.core.game.model.ObjectModel;
import org.smallbox.faraway.core.module.GameModule;
import org.smallbox.faraway.core.module.ModuleInfo;

import java.io.File;

/**
 * Created by Alex on 09/10/2015.
 */
public class LuaModule extends GameModule {
    private final File          _directory;
    private boolean             _isLoaded;

    public LuaModule(File directory) {
        _directory = directory;
    }

    public File getDirectory() {
        return _directory;
    }

    @Override
    protected void onLoaded() {
    }

    @Override
    protected void onUpdate(int tick) {
    }

    public void setActivate(boolean isActivate) { _isLoaded = isActivate; }
}
