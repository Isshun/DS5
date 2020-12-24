package org.smallbox.faraway.core.engine.module.java;

import org.smallbox.faraway.core.engine.module.ModuleInfo;

import java.io.File;

public class ThirdPartyModule {
    private final ModuleInfo    _info;
    private final File          _directory;

    public ThirdPartyModule(ModuleInfo info, File directory) {
        _info = info;
        _directory = directory;
    }

    public File getDirectory() {
        return _directory;
    }

    public ModuleInfo getInfo() {
        return _info;
    }
}
