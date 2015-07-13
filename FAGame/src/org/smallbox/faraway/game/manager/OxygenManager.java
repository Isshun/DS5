package org.smallbox.faraway.game.manager;

import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.model.room.NeighborModel;
import org.smallbox.faraway.game.model.room.RoomModel;

/**
 * Created by Alex on 18/06/2015.
 */
public class OxygenManager extends BaseManager {
    private int _oxygen;

    public OxygenManager() {
        _updateInterval = 10;
    }

    @Override
    protected void onCreate() {
        _oxygen = (int)(Game.getInstance().getPlanet().getOxygen() * 100);
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
        _oxygen = (int)(oxygen * 100);
    }

    public double getOxygen() {
        return _oxygen / 100.0;
    }

    public void increaseOxygen() {
        _oxygen += 10;
    }

    public void decreaseOxygen() {
        _oxygen += 10;
    }
}
