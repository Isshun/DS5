package org.smallbox.faraway.core.module.lua;

import org.smallbox.faraway.core.game.model.ObjectModel;
import org.smallbox.faraway.core.module.ModuleInfo;

import java.io.File;

/**
 * Created by Alex on 09/10/2015.
 */
public class LuaModule extends ObjectModel {
    private final ModuleInfo    _info;
    private final File          _directory;
    private boolean             _isLoaded;

    public LuaModule(ModuleInfo info, File directory) {
        _info = info;
        _directory = directory;
    }

    public ModuleInfo getInfo() {
        return _info;
    }

    public File getDirectory() {
        return _directory;
    }

    public boolean isLoaded() { return _isLoaded; }
    public void setActivate(boolean isActivate) { _isLoaded = isActivate; }
}
