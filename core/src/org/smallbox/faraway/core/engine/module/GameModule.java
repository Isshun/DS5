package org.smallbox.faraway.core.engine.module;

import org.smallbox.faraway.core.data.serializer.SerializerInterface;
import org.smallbox.faraway.core.game.GameObserver;

import java.util.ArrayList;
import java.util.Collection;

public abstract class GameModule extends AbsGameModule implements GameObserver {
    private Collection<SerializerInterface> _serializers = new ArrayList<>();

    public Collection<SerializerInterface> getSerializers() { return _serializers; }
}