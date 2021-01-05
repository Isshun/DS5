package org.smallbox.faraway.core.game;

import org.smallbox.faraway.core.module.world.SQLManager;
import org.smallbox.faraway.util.log.Log;

public abstract class GameSerializer {
    public abstract void onSave(SQLManager sqlManager);
    public abstract void onLoad(SQLManager sqlManager);
    public int getModulePriority() { return 999; }

    public void save(SQLManager sqlManager) {
        Log.info("Call onSave on " + getClass().getSimpleName());
        onSave(sqlManager);
    }

    public void load(SQLManager sqlManager) {
        Log.info("Call onLoadModule on " + getClass().getSimpleName());
        onLoad(sqlManager);
    }

}
