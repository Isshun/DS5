package org.smallbox.faraway.core.game;

import org.smallbox.faraway.core.save.SQLManager;
import org.smallbox.faraway.util.log.Log;

public abstract class GameSerializer {

    public abstract void onSave(SQLManager sqlManager);

    public abstract void onLoad(SQLManager sqlManager);

    public GameSerializerPriority getPriority() {
        return GameSerializerPriority.NO_PRIORITY;
    }

    public void save(SQLManager sqlManager) {
        Log.debug("Call onSave on " + getClass().getSimpleName());
        onSave(sqlManager);
    }

    public void load(SQLManager sqlManager) {
        Log.debug("Call onLoadModule on " + getClass().getSimpleName());
        onLoad(sqlManager);
    }

}
