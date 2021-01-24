package org.smallbox.faraway.core.module;

import org.smallbox.faraway.core.game.GameObserver;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class SuperGameModule2<T_OBSERVER extends ModuleObserver> extends AbsGameModule implements GameObserver {

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