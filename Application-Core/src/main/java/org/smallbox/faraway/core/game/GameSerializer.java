package org.smallbox.faraway.core.game;

import org.smallbox.faraway.core.GameException;
import org.smallbox.faraway.core.engine.module.AbsGameModule;
import org.smallbox.faraway.core.module.ModuleSerializer;
import org.smallbox.faraway.core.module.world.SQLManager;
import org.smallbox.faraway.util.log.Log;

public abstract class GameSerializer<T_MODULE> {
    public abstract void onSave(SQLManager sqlManager, T_MODULE module, Game game);
    public abstract void onLoad(SQLManager sqlManager, T_MODULE module, Game game, Data data);
    public int getModulePriority() { return 0; }

    public void save(SQLManager sqlManager, T_MODULE module, Game game) {
        Log.info("Serializer: call onSave on " + getClass().getName());
        onSave(sqlManager, module, game);
    }

    public void load(SQLManager sqlManager, T_MODULE module, Game game, Data data) {
        Log.info("Serializer: call onLoadModule " + getClass().getName());
        onLoad(sqlManager, module, game, data);
    }

    public static GameSerializer<AbsGameModule> createSerializer(AbsGameModule module) {
        Class<? extends GameSerializer> cls = module.getClass().getAnnotation(ModuleSerializer.class).value();
        try {
            return cls.newInstance();
        } catch ( IllegalAccessException | InstantiationException e) {
            throw new GameException(GameSerializer.class, e, "Unable to create serializer for " + cls.getSimpleName());
        }
    }
}
