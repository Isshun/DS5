package org.smallbox.faraway.module.world;

import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.engine.module.java.ModuleManager;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.module.room.RoomModule;
import org.smallbox.faraway.core.game.module.room.model.RoomConnectionModel;
import org.smallbox.faraway.core.game.module.room.model.RoomModel;
import org.smallbox.faraway.core.game.module.world.WeatherModule;
import org.smallbox.faraway.core.game.module.world.model.item.ItemModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 13/06/2015.
 */
public class TemperatureModule extends GameModule {
    private List<ItemModel>     _items = new ArrayList<>();

    @Override
    public void onLoaded(Game game) {
        _updateInterval = 10;
    }

    @Override
    protected boolean loadOnStart() {
        return true;
    }

    @Override
    public void onAddItem(ItemModel item) {
        if (item.getInfo().hasTemperatureEffect()) {
            _items.add(item);
        }
    }

    @Override
    public void onRemoveItem(ItemModel item) {
        _items.remove(item);
    }

    public void onUpdate(int tick) {
        WeatherModule weatherModule = (WeatherModule) ModuleManager.getInstance().getModule(WeatherModule.class);
        RoomModule roomModule = (RoomModule) ModuleManager.getInstance().getModule(RoomModule.class);
        if (roomModule != null) {
            for (RoomModel room : roomModule.getRooms()) {
                if (room.isExterior()) {
                    room.setTemperature(weatherModule.getTemperature(room.getFloor()));
                } else {
                    // Mix temperature with neighbors
                    for (RoomConnectionModel roomConnection: room.getConnections()) {
                        int totalSize = room.getSize() + roomConnection.getRoom().getSize();
                        double totalTemperature = (room.getTemperature() * room.getSize()) + (roomConnection.getRoom().getTemperature() * roomConnection.getRoom().getSize());
                        updateRoomTemperature(room, totalTemperature / totalSize, roomConnection.getPermeability());
                        updateRoomTemperature(roomConnection.getRoom(), totalTemperature / totalSize, roomConnection.getPermeability());
                    }

                    // Get temperature from objects
                    room.getParcels().forEach(parcel -> {
                    });
                }
            }
        }
    }

    private void updateRoomTemperature(RoomModel room, double temperature, double connectionValue) {
        double diff = temperature - room.getTemperature();
        double ratio = 1;
        if (Math.abs(diff) > 5) ratio = 0.5;
        if (Math.abs(diff) > 10) ratio = 0.1;
        room.setTemperature(room.getTemperature() + (diff * connectionValue * ratio));
    }

    @Override
    public void onTemperatureChange(double temperature) {
    }
}
