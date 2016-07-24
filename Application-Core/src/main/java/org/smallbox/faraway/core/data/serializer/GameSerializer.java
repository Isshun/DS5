package org.smallbox.faraway.core.data.serializer;

import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.util.Log;

public abstract class GameSerializer {
    public abstract void onSave(Game game);
    public abstract void onLoad(Game game);
    public int getModulePriority() { return 0; }

    public void save(Game game) {
        Log.info("Serializer: call onSave on " + getClass().getName());
        onSave(game);
    }

    public void load(Game game) {
        Log.info("Serializer: call onLoad " + getClass().getName());
        onLoad(game);
    }
}
