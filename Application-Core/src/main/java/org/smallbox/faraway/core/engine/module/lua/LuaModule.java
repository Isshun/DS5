package org.smallbox.faraway.core.engine.module.lua;

import org.smallbox.faraway.core.engine.module.AbsGameModule;
import org.smallbox.faraway.core.game.Game;

import java.io.File;

public class LuaModule extends AbsGameModule {
    private final File          _directory;
    private boolean             _isActivate;

    public LuaModule(File directory) {
        _directory = directory;
    }

    @Override
    protected void onGameUpdate(Game game, int tick) {
    }

    public void setActivate(boolean isActivate) { _isActivate = isActivate; }

    public File getDirectory() {
        return _directory;
    }

    @Override
    public boolean isLoaded() { return true; }
}
