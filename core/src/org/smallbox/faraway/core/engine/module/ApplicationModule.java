package org.smallbox.faraway.core.engine.module;

/**
 * Created by Alex on 26/11/2015.
 */
public abstract class ApplicationModule extends ModuleBase {
    @Override
    public boolean loadOnStart() {
        return true;
    }
}
