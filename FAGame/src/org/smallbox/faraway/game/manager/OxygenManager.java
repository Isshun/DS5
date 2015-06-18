package org.smallbox.faraway.game.manager;

import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.model.room.NeighborModel;
import org.smallbox.faraway.game.model.room.RoomModel;

import java.util.List;

/**
 * Created by Alex on 18/06/2015.
 */
public class OxygenManager extends BaseManager {
    @Override
    protected void onUpdate(int tick) {
        List<RoomModel> rooms = Game.getRoomManager().getRoomList();
        for (RoomModel room: rooms) {
            if (room.isExterior()) {
                room.setOxygen(Game.getInstance().getPlanet().getOxygen());
            } else {
                for (NeighborModel neighbor: room.getNeighbors()) {
                    room.setOxygen(room.getOxygen() + (neighbor.room.getOxygen() - room.getOxygen()) * (1 - neighbor.sealing) * 0.05);
                }
            }
        }
    }
}
