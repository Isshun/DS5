package org.smallbox.faraway.client;

import org.smallbox.faraway.client.ui.event.GameEvent;
import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.core.game.GameObserver;
import org.smallbox.faraway.game.world.Parcel;

import java.util.List;

public interface GameClientObserver extends GameObserver {
    default void onReloadUI(){}
    default void onRefreshUI(int frame){}
    default void onDeselect() {}
//    default void onOverParcel(ParcelModel parcel) {}
//    default void onSelectArea(AreaModel area) {}
//    default boolean onSelectCharacter(CharacterModel character) {return false;}
    default boolean onSelectParcelOld(Parcel parcel) {return false;}
    default void onKeyPressWithEvent(GameEvent event, int key) {
        if (!event.consumed && onKeyPress(key)) {
            event.consume();
        }
    }
    default boolean onKeyPress(int key) { return false; }
    default void onKeyEvent(GameEventListener.Action action, int key, GameEventListener.Modifier modifier) {}

//    default void onGameRender(Game game) {}
    default void onFloorUp() {}
    default void onFloorDown() {}
    default void onFloorChange(int floor) {}

    default void onMouseMove(int x, int y, int button) {}
    default void onMousePress(int x, int y, int button) {}
    default void onMouseRelease(int x, int y, int button) {}

    default void onClickOnMap(GameEvent mouseEvent) {}

    /**
     * Retourne une liste des parcels sélectionnées par le joueurs
     *
     * @param parcels Selected parcels
     * @return true if event has been consumed
     */
    default boolean onClickOnParcel(List<Parcel> parcels) { return false; }

    default void onClick(int x, int y) {}
}
