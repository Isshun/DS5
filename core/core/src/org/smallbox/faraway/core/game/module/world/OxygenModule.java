package org.smallbox.faraway.core.game.module.world;

import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.engine.module.java.ModuleHelper;
import org.smallbox.faraway.core.engine.module.java.ModuleManager;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.module.room.RoomModule;
import org.smallbox.faraway.core.game.module.room.model.RoomConnectionModel;
import org.smallbox.faraway.core.game.module.room.model.RoomModel;
import org.smallbox.faraway.core.game.module.world.model.item.ItemModel;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Alex on 18/06/2015.
 */
public class OxygenModule extends GameModule {
    private double                  _oxygen;
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
        return true;
    }

    @Override
    protected void onUpdate(int tick) {
        RoomModule roomModule = (RoomModule) ModuleManager.getInstance().getModule(RoomModule.class);
        if (roomModule != null) {

//            // Reset rooms pressure
//            roomModule.getRooms().forEach(room -> room.setPressure(room.getSize()));
//
//            _items.forEach(item -> {
//                if (item.getParcel().getRoom() != null) {
//                    item.getParcel().getRoom().setPressure(item.getParcel().getRoom().getPressure() + (item.getInfo().effects.oxygen * item.getInfo().effects.pressure));
//                }
//            });
//
//            _items.forEach(item -> {
//                if (item.getParcel().getRoom() != null) {
//                    RoomModel room = item.getParcel().getRoom();
//                    changeOxygenSmooth(room, room.getPressure() / room.getSize(), 1);
//                }
//            });

            roomModule.getRooms().forEach(room -> {
                        if (room.isExterior()) {
                            room.setOxygen(_oxygen);
                        } else {
                            // Mix oxygen with neighbors
                            for (RoomConnectionModel roomConnection: room.getConnections()) {
                                int totalSize = room.getSize() + roomConnection.getRoom().getSize();
                                double totalOxygen = (room.getOxygen() * room.getSize()) + (roomConnection.getRoom().getOxygen() * roomConnection.getRoom().getSize());
                                updateRoomPressure(room, totalOxygen / totalSize, roomConnection.getPermeability());
                                updateRoomPressure(roomConnection.getRoom(), totalOxygen / totalSize, roomConnection.getPermeability());
                            }

                            // Get oxygen from objects
                            room.getParcels().forEach(parcel -> {
//                        effects
                            });
                        }
                    }
            );
        }
    }

    private void updateRoomPressure(RoomModel room, double oxygen, double connectionValue) {
        double diff = oxygen - room.getOxygen();
        double ratio = 1;
        if (Math.abs(diff) > 0.25) ratio = 0.5;
        if (Math.abs(diff) > 0.5) ratio = 0.1;
        room.setOxygen(room.getOxygen() + (diff * connectionValue * ratio));
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