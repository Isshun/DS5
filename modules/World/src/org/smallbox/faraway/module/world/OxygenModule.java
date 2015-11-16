package org.smallbox.faraway.module.world;

import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.model.Data;
import org.smallbox.faraway.core.game.module.room.model.NeighborModel;
import org.smallbox.faraway.core.game.module.room.model.RoomModel;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.engine.module.java.ModuleManager;
import org.smallbox.faraway.core.game.module.room.RoomModule;

/**
 * Created by Alex on 18/06/2015.
 */
public class OxygenModule extends GameModule {
    private double _oxygen;

    public OxygenModule() {
        _updateInterval = 10;
    }

    @Override
    protected void onLoaded(Game game) {
        _oxygen = game.getPlanet().getOxygen();
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
                    // Mix oxygen with neighbors
                    for (NeighborModel neighbor : room.getNeighbors()) {
                        double totalOxygen = (room.getOxygen() * room.getSize()) + (neighbor.getRoom().getOxygen() * neighbor.getRoom().getSize());
                        int totalSize = room.getSize() + neighbor.getRoom().getSize();
                        double ratio = neighbor.getBorderSize() / room.getSize();
                        double targetOxygen = totalOxygen / totalSize;
                        changeOxygenSmooth(room, targetOxygen, ratio);
                    }
                    // Get oxygen from objects
                    room.getParcels().forEach(parcel -> {
                    });
                }
            }
        }
    }

    private void changeOxygenSmooth(RoomModel room, double targetOxygen, double ratio) {
        double diff = targetOxygen - room.getOxygen();
        if (diff > 0.5 || diff < -0.5) {
            room.setOxygen(room.getOxygen() + (diff * ratio * 0.75));
        } else if (diff > 0.25 || diff < -0.25) {
            room.setOxygen(room.getOxygen() + (diff * ratio * 0.5));
        } else if (diff > 0.001 || diff < -0.001) {
            room.setOxygen(room.getOxygen() + (diff * ratio * 0.25));
        }
    }
}
