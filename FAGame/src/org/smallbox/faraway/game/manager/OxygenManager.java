package org.smallbox.faraway.game.manager;

import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.model.room.NeighborModel;
import org.smallbox.faraway.game.model.room.RoomModel;

/**
 * Created by Alex on 18/06/2015.
 */
public class OxygenManager extends BaseManager {

    public OxygenManager() {
        _updateInterval = 10;
    }

    @Override
    protected void onUpdate(int tick) {
        RoomManager roomManager = (RoomManager)Game.getInstance().getManager(RoomManager.class);
        if (roomManager != null) {
            double exteriorLevel = Game.getInstance().getPlanet().getOxygen();
            for (RoomModel room : roomManager.getRoomList()) {
                if (room.isExterior()) {
                    room.setOxygen(exteriorLevel);
                } else {
                    for (NeighborModel neighbor : room.getNeighbors()) {
                        room.setOxygen(room.getOxygen() + (neighbor.room.getOxygen() - room.getOxygen()) * (1 - neighbor.sealing) * 0.05);
                    }
                }
            }

            roomManager.getRoomList().forEach(room -> room.getParcels().forEach(parcel -> parcel.setOxygen(room.getOxygen())));
            roomManager.getRoomlessParcels().forEach(parcel -> parcel.setOxygen(exteriorLevel));
        }
    }
}
