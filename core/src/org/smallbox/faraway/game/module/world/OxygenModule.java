package org.smallbox.faraway.game.module.world;

import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.model.GameData;
import org.smallbox.faraway.game.model.room.NeighborModel;
import org.smallbox.faraway.game.model.room.RoomModel;
import org.smallbox.faraway.game.module.GameModule;
import org.smallbox.faraway.game.module.ModuleManager;

/**
 * Created by Alex on 18/06/2015.
 */
public class OxygenModule extends GameModule {
    private int _oxygen;

    public OxygenModule() {
        _updateInterval = 10;
    }

    @Override
    protected void onLoaded() {
        _oxygen = (int)(Game.getInstance().getPlanet().getOxygen() * 100);
    }

    @Override
    protected boolean loadOnStart() {
        return GameData.config.manager.oxygen;
    }

    @Override
    protected void onUpdate(int tick) {
        RoomModule roomModule = (RoomModule) ModuleManager.getInstance().getModule(RoomModule.class);
        if (roomModule != null) {
            for (RoomModel room : roomModule.getRoomList()) {
                if (room.isExterior()) {
                    room.setOxygen(_oxygen);
                } else {
                    for (NeighborModel neighbor : room.getNeighbors()) {
                        room.setOxygen(room.getOxygen() + (neighbor.room.getOxygen() - room.getOxygen()) * (1 - neighbor.sealing) * 0.05);
                    }
                }
            }

            roomModule.getRoomList().forEach(room -> room.getParcels().forEach(parcel -> parcel.setOxygen(room.getOxygen())));
            roomModule.getRoomlessParcels().forEach(parcel -> parcel.setOxygen(_oxygen));
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
