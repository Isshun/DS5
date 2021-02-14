package org.smallbox.faraway.core.lua;

import org.smallbox.faraway.core.module.ModuleBase;

import java.io.File;

public class LuaModule extends ModuleBase {
    private final File          _directory;
    private boolean             _isActivate;

    public LuaModule(File directory) {
        _directory = directory;
    }

    public void setActivate(boolean isActivate) { _isActivate = isActivate; }

    public File getDirectory() {
        return _directory;
    }

}
