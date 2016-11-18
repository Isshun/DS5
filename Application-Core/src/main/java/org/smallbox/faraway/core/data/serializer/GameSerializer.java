package org.smallbox.faraway.core.data.serializer;

import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.util.Log;

public abstract class GameSerializer<T_MODULE extends GameModule> {
    public abstract void onSave(T_MODULE module, Game game);
    public abstract void onLoad(T_MODULE module, Game game);
    public int getModulePriority() { return 0; }

    public void save(T_MODULE module, Game game) {
        Log.info("Serializer: call onSave on " + getClass().getName());
        onSave(module, game);
    }

    public void load(T_MODULE module, Game game) {
        Log.info("Serializer: call onLoad " + getClass().getName());
        onLoad(module, game);
    }
}
