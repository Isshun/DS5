//package org.smallbox.faraway.client;
//
//import org.smallbox.faraway.core.Application;
//import org.smallbox.faraway.core.GameException;
//import org.smallbox.faraway.core.engine.module.AbsGameModule;
//import org.smallbox.faraway.core.engine.module.ModuleObserver;
//import org.smallbox.faraway.core.module.world.model.ParcelModel;
//import org.smallbox.faraway.modules.character.model.base.CharacterModel;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.function.Consumer;
//
///**
// * Created by Alex on 15/01/2017.
// */
//public class GameClientModule<T extends ModuleObserver> extends AbsGameModule implements GameClientObserver {
//    private List<T> _observers = new ArrayList<>();
//
//    public boolean onSelectParcel(ParcelModel parcel) {
//        return false;
//    }
//
//    public boolean onSelectCharacter(CharacterModel character) {
//        return false;
//    }
//
//    public void addObserver(T observer) {
//        // TODO
////        if (Application.gameManager.getGame().getState() != Game.GameStatus.INITIALIZED) {
////            throw new GameException("GameModule: Add observer from initialized module (module: %s)", getClass().getName());
////        }
//
//        _observers.add(observer);
//    }
//
//    public void notifyObservers(Consumer<T> action) {
//        try {
//            _observers.forEach(action);
//        } catch (Exception e) {
//            Application.setRunning(false);
//            throw new GameException(ApplicationClient.class, e, "Error during notify");
//        }
//    }
//}
