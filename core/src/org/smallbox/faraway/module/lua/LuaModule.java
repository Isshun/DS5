package org.smallbox.faraway.module.lua;

import org.smallbox.faraway.module.ModuleInfo;
import org.smallbox.faraway.game.model.ObjectModel;

import java.io.File;

/**
 * Created by Alex on 09/10/2015.
 */
public class LuaModule extends ObjectModel {
    private final ModuleInfo _info;
    private final File          _directory;
    private boolean             _isActivate;

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

    public boolean isActivate() { return _isActivate; }
    public void setActivate(boolean isActivate) { _isActivate = isActivate; }
}
