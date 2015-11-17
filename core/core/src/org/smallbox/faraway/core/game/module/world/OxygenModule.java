package org.smallbox.faraway.core.game.module.world;

import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.engine.module.java.ModuleHelper;
import org.smallbox.faraway.core.engine.module.java.ModuleManager;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.model.Data;
import org.smallbox.faraway.core.game.module.room.RoomModule;
import org.smallbox.faraway.core.game.module.room.model.NeighborModel;
import org.smallbox.faraway.core.game.module.room.model.RoomModel;
import org.smallbox.faraway.core.game.module.world.model.item.ItemModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Alex on 18/06/2015.
 */
public class OxygenModule extends GameModule {
    private double                  _oxygen;
    private List<NeighborModel>     _openList = new ArrayList<>();
    private List<NeighborModel>     _closeList = new ArrayList<>();
    private List<ItemModel>         _items;

    public OxygenModule() {
        _updateInterval = 10;
    }

    public double getOxygen() { return _oxygen; }

    @Override
    protected void onLoaded(Game game) {
        _oxygen = game.getPlanet().getOxygen();
        _items = ModuleHelper.getWorldModule().getItems().stream().filter(item -> item.getInfo().effects != null && item.getInfo().effects.oxygen > 0).collect(Collectors.toList());
    }

    @Override
    protected boolean loadOnStart() {
        return Data.config.manager.oxygen;
    }

    @Override
    protected void onUpdate(int tick) {
        RoomModule roomModule = (RoomModule) ModuleManager.getInstance().getModule(RoomModule.class);
        if (roomModule != null) {

            // Reset rooms pressure
            roomModule.getRooms().forEach(room -> room.setPressure(room.getSize()));

            _items.forEach(item -> {
                if (item.getParcel().getRoom() != null) {
                    item.getParcel().getRoom().setPressure(item.getParcel().getRoom().getPressure() + item.getInfo().effects.pressure);
                }
            });

            _items.forEach(item -> {
                if (item.getParcel().getRoom() != null) {
                    RoomModel room = item.getParcel().getRoom();
                    room.setOxygen((room.getOxygen() * room.getSize()) + (item.getInfo().effects.oxygen * item.getInfo().effects.pressure));
                }
            });

//            for (RoomModel room : roomModule.getRooms()) {
//                if (room.isExterior()) {
//                    room.setOxygen(_oxygen);
//                } else {
//                    // Mix oxygen with neighbors
//                    exploreRoom(room);
//
//                    // Get oxygen from objects
//                    room.getParcels().forEach(parcel -> {
////                        effects
//                    });
//                }
//            }
        }
    }

    private void exploreRoom(RoomModel room) {
        double totalOxygen = room.getOxygen() * room.getSize();
        int totalSize = room.getSize();

        _openList.clear();
        _openList.addAll(room.getNeighbors());
        _closeList.clear();
        while (!_openList.isEmpty()) {
            NeighborModel neighbor = _openList.remove(0);
            totalOxygen += neighbor.getRoom().getOxygen() * Math.min(1000, neighbor.getRoom().getSize()) * (1 - neighbor.getBorderValue());
            totalSize += Math.min(1000, neighbor.getRoom().getSize()) * (1 - neighbor.getBorderValue());
            _closeList.add(neighbor);

            for (NeighborModel n : neighbor.getRoom().getNeighbors()) {
                if (!_closeList.contains(n)) {
                    _openList.add(n);
                }
            }
        }

        room.setTargetOxygen(totalOxygen / totalSize);
        room.setTargetOxygenPression(totalSize);
        changeOxygenSmooth(room, totalOxygen / totalSize, 1);
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

    @Override
    public void onAddItem(ItemModel item) {
        _items.remove(item);
    }

    @Override
    public void onRemoveItem(ItemModel item) {
        if (item.getInfo().effects != null && item.getInfo().effects.oxygen > 0) {
            _items.add(item);
        }
    }
}