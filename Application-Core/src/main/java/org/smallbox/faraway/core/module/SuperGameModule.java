package org.smallbox.faraway.core.module;

import org.smallbox.faraway.game.world.ObjectModel;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class SuperGameModule<T_MODEL extends ObjectModel, T_OBSERVER extends ModuleObserver> extends GenericGameModule<T_MODEL> {

    private final List<T_OBSERVER> _observers = new ArrayList<>();

    public void addObserver(T_OBSERVER observer) {
        // TODO
//        if (Application.gameManager.getGame().getState() != Game.GameStatus.JOB_INITIALIZED) {
//            throw new GameException("GameModule: Add observer from initialized module (module: %s)", getClass().getName());
//        }

        _observers.add(observer);
    }

    public void notifyObservers(Consumer<T_OBSERVER> action) {
        try {
            _observers.forEach(action);
        } catch (Error | RuntimeException e) {
            e.printStackTrace();
        }
    }
}