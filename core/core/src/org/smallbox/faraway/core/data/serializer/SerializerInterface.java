package org.smallbox.faraway.core.data.serializer;

import org.smallbox.faraway.core.game.GameInfo;

public abstract class SerializerInterface {
    public abstract void save();
    public abstract void load(GameInfo gameInfo);
    public int getModulePriority() { return 0; }
}
