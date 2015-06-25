package org.smallbox.faraway.game.manager;

import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.model.room.NeighborModel;
import org.smallbox.faraway.game.model.room.RoomModel;

/**
 * Created by Alex on 18/06/2015.
 */
public class OxygenManager extends BaseManager {
    @Override
    protected void onUpdate(int tick) {
        if (Game.getRoomManager() != null) {
            for (RoomModel room : Game.getRoomManager().getRoomList()) {
                if (room.isExterior()) {
                    room.setOxygen(Game.getInstance().getPlanet().getOxygen());
                } else {
                    for (NeighborModel neighbor : room.getNeighbors()) {
                        room.setOxygen(room.getOxygen() + (neighbor.room.getOxygen() - room.getOxygen()) * (1 - neighbor.sealing) * 0.05);
                    }
                }
            }
        }
    }
}
