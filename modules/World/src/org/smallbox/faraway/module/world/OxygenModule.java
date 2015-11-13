package org.smallbox.faraway.module.world;

import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.model.Data;
import org.smallbox.faraway.core.game.module.room.model.NeighborModel;
import org.smallbox.faraway.core.game.module.room.model.RoomModel;
import org.smallbox.faraway.core.module.GameModule;
import org.smallbox.faraway.core.module.java.ModuleManager;
import org.smallbox.faraway.core.game.module.room.RoomModule;

/**
 * Created by Alex on 18/06/2015.
 */
public class OxygenModule extends GameModule {
    private int _oxygen;

    public OxygenModule() {
        _updateInterval = 10;
    }

    @Override
    protected void onLoaded(Game game) {
//        _oxygen = (int)(Game.getInstance().getPlanet().getOxygen() * 100);
    }

    @Override
    protected boolean loadOnStart() {
        return Data.config.manager.oxygen;
    }

    @Override
    protected void onUpdate(int tick) {
        RoomModule roomModule = (RoomModule) ModuleManager.getInstance().getModule(RoomModule.class);
        if (roomModule != null) {
            for (RoomModel room : roomModule.getRooms()) {
                if (room.isExterior()) {
                    room.setOxygen(_oxygen);
                } else {
                    for (NeighborModel neighbor : room.getNeighbors()) {
                        room.setOxygen(room.getOxygen() + (neighbor._room.getOxygen() - room.getOxygen()) * (1 - neighbor._borderValue) * 0.05);
                    }
                }
            }

//            roomModule.getRooms().forEach(room -> room.getParcels().forEach(parcel -> parcel.setOxygen(room.getOxygen())));
//            roomModule.getRoomlessParcels().forEach(parcel -> parcel.setOxygen(_oxygen));
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
