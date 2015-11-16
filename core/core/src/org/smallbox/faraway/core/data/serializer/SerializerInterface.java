package org.smallbox.faraway.core.data.serializer;

import org.smallbox.faraway.core.game.Game;

public abstract class SerializerInterface {
    public abstract void save();
    public abstract void load(Game game);
    public int getModulePriority() { return 0; }
}
