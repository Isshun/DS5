package org.smallbox.faraway.client;

import org.smallbox.faraway.client.ui.engine.GameEvent;
import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameObserver;
import org.smallbox.faraway.modules.area.AreaModel;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;

import java.util.List;

/**
 * Created by Alex on 15/01/2017.
 */
public interface GameClientObserver extends GameObserver {
    default void onReloadUI(){}
    default void onRefreshUI(int frame){}
    default void onDeselect() {}
//    default void onOverParcel(ParcelModel parcel) {}
    default void onSelectArea(AreaModel area) {}
    default boolean onSelectCharacter(CharacterModel character) {return false;}
    default boolean onSelectParcel(ParcelModel parcel) {return false;}
    default void onKeyPressWithEvent(GameEvent event, GameEventListener.Key key) {
        if (!event.consumed && onKeyPress(key)) {
            event.consume();
        }
    }
    default boolean onKeyPress(GameEventListener.Key key) { return false; }
    default void onKeyEvent(GameEventListener.Action action, GameEventListener.Key key, GameEventListener.Modifier modifier) {}

    default void onGameRender(Game game) {}
    default void onFloorUp() {}
    default void onFloorDown() {}
    default void onFloorChange(int floor) {}

    default void onMouseMove(GameEvent event) {}
    default void onMousePress(GameEvent event) {}
    default void onMouseRelease(GameEvent event) {}

    default void onClickOnMap(GameEvent mouseEvent) {}

    /**
     * Retourne une liste des parcels sélectionnées par le joueurs
     *
     * @param parcels Selected parcels
     * @return true if event has been consumed
     */
    default boolean onClickOnParcel(List<ParcelModel> parcels) { return false; }

    default void onClick(int x, int y) {}
}
