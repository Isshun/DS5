package org.smallbox.faraway.core.game;

import org.smallbox.faraway.core.GameException;
import org.smallbox.faraway.core.engine.module.AbsGameModule;
import org.smallbox.faraway.core.module.ModuleSerializer;
import org.smallbox.faraway.util.Log;

public abstract class GameSerializer<T_MODULE> {
    public abstract void onSave(T_MODULE module, Game game);
    public abstract void onLoad(T_MODULE module, Game game);
    public int getModulePriority() { return 0; }

    public void save(T_MODULE module, Game game) {
        Log.info("Serializer: call onSave on " + getClass().getName());
        onSave(module, game);
    }

    public void load(T_MODULE module, Game game) {
        Log.info("Serializer: call onLoadModule " + getClass().getName());
        onLoad(module, game);
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
