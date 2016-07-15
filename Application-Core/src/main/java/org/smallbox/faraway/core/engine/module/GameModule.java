package org.smallbox.faraway.core.engine.module;

import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.data.serializer.SerializerInterface;
import org.smallbox.faraway.core.game.GameObserver;
import org.smallbox.faraway.core.game.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public abstract class GameModule<T extends ModuleObserver> extends AbsGameModule implements GameObserver {
    private Collection<SerializerInterface> _serializers = new ArrayList<>();
    private List<T> _observers = new ArrayList<>();

    public Collection<SerializerInterface> getSerializers() { return _serializers; }

    public boolean onSelectParcel(ParcelModel parcel) {
        return false;
    }

    public boolean onSelectCharacter(CharacterModel character) {
        return false;
    }

    public void addObserver(T observer) {
        _observers.add(observer);
    }

    public void notifyObservers(Consumer<T> action) {
        try {
            _observers.stream().forEach(action);
        } catch (Error | RuntimeException e) {
            Application.getInstance().setRunning(false);
            e.printStackTrace();
        }
    }
}