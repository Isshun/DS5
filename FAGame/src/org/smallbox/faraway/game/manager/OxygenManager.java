package org.smallbox.faraway.game.manager;

import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.model.room.NeighborModel;
import org.smallbox.faraway.game.model.room.RoomModel;

/**
 * Created by Alex on 18/06/2015.
 */
public class OxygenManager extends BaseManager {
    private double _oxygen;

    public OxygenManager() {
        _updateInterval = 10;
    }

    @Override
    protected void onCreate() {
        _oxygen = Game.getInstance().getPlanet().getOxygen();
    }

    @Override
    protected void onUpdate(int tick) {
        RoomManager roomManager = (RoomManager)Game.getInstance().getManager(RoomManager.class);
        if (roomManager != null) {
            for (RoomModel room : roomManager.getRoomList()) {
                if (room.isExterior()) {
                    room.setOxygen(_oxygen);
                } else {
                    for (NeighborModel neighbor : room.getNeighbors()) {
                        room.setOxygen(room.getOxygen() + (neighbor.room.getOxygen() - room.getOxygen()) * (1 - neighbor.sealing) * 0.05);
                    }
                }
            }

            roomManager.getRoomList().forEach(room -> room.getParcels().forEach(parcel -> parcel.setOxygen(room.getOxygen())));
            roomManager.getRoomlessParcels().forEach(parcel -> parcel.setOxygen(_oxygen));
        }
    }

    public void setOxygen(double oxygen) {
        _oxygen = oxygen;
    }

    public double getOxygen() {
        return _oxygen;
    }
}
