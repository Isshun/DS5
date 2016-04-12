package org.smallbox.faraway.core.engine.module.lua;

import org.smallbox.faraway.core.engine.module.ModuleBase;
import org.smallbox.faraway.core.game.Game;

import java.io.File;

/**
 * Created by Alex on 09/10/2015.
 */
public class LuaModule extends ModuleBase {
    private final File          _directory;
    private boolean             _isLoaded;

    public LuaModule(File directory) {
        _directory = directory;
    }

    @Override
    protected void onGameStart(Game game) {
    }

    @Override
    protected void onGameUpdate(int tick) {
    }

    @Override
    public boolean loadOnStart() {
        return false;
    }

    public void setActivate(boolean isActivate) { _isLoaded = isActivate; }

    public File getDirectory() {
        return _directory;
    }
}
