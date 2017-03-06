package org.smallbox.faraway.core.engine.module;

import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameObserver;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class GameModule<T extends ModuleObserver> extends AbsGameModule implements GameObserver {
    private List<T> _observers = new ArrayList<>();

    public boolean onSelectParcel(ParcelModel parcel) {
        return false;
    }

    public boolean onSelectCharacter(CharacterModel character) {
        return false;
    }

    public void addObserver(T observer) {
        // TODO
//        if (Application.gameManager.getGame().getState() != Game.GameStatus.INITIALIZED) {
//            throw new GameException("GameModule: Add observer from initialized module (module: %s)", getClass().getName());
//        }

        _observers.add(observer);
    }

    @Override
    public final void onGameCreateObserver(Game game) {

    }

    public void notifyObservers(Consumer<T> action) {
        try {
            _observers.forEach(action);
        } catch (Error | RuntimeException e) {
            Application.setRunning(false);
            e.printStackTrace();
        }
    }
}