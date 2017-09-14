package org.smallbox.faraway.common.lua;

import org.smallbox.faraway.common.AbsGameModule;

import java.io.File;

/**
 * Created by Alex on 09/10/2015.
 */
public class LuaModule extends AbsGameModule {
    private final File          _directory;
    private boolean             _isActivate;

    public LuaModule(File directory) {
        _directory = directory;
    }

    @Override
    protected void onGameUpdate(int tick) {
    }

    public void setActivate(boolean isActivate) { _isActivate = isActivate; }

    public File getDirectory() {
        return _directory;
    }

    @Override
    public boolean isLoaded() { return true; }
}
