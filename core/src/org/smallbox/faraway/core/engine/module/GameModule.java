package org.smallbox.faraway.core.engine.module;

import org.smallbox.faraway.core.data.serializer.SerializerInterface;
import org.smallbox.faraway.core.game.GameObserver;
import org.smallbox.faraway.core.game.module.character.CharacterModule;
import org.smallbox.faraway.core.game.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;

import java.util.ArrayList;
import java.util.Collection;

public abstract class GameModule extends AbsGameModule implements GameObserver {
    private Collection<SerializerInterface> _serializers = new ArrayList<>();

    public Collection<SerializerInterface> getSerializers() { return _serializers; }

    public boolean onSelectParcel(ParcelModel parcel) {
        return false;
    }

    public boolean onSelectCharacter(CharacterModel character) {
        return false;
    }
}