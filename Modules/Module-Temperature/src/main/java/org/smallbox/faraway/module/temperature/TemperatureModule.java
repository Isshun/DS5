package org.smallbox.faraway.module.temperature;

import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.module.room.model.RoomConnectionModel;
import org.smallbox.faraway.core.module.room.model.RoomModel;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.module.item.ItemModule;
import org.smallbox.faraway.module.item.ItemModuleObserver;
import org.smallbox.faraway.module.item.UsableItem;
import org.smallbox.faraway.module.room.RoomModule;
import org.smallbox.faraway.module.weather.WeatherModule;
import org.smallbox.faraway.module.weather.WeatherModuleObserver;
import org.smallbox.faraway.module.world.WorldModule;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 13/06/2015.
 */
public class TemperatureModule extends GameModule {
    @BindModule
    private WorldModule worldModule;

    @BindModule
    private WeatherModule weatherModule;

    @BindModule
    private RoomModule roomModule;

    @BindModule
    private ItemModule itemModule;

    private List<UsableItem> _items = new ArrayList<>();

    @Override
    public void onGameCreate(Game game) {
        itemModule.addObserver(new ItemModuleObserver() {
            @Override
            public void onRemoveItem(ParcelModel parcel, UsableItem item) {
                _items.remove(item);
            }

            @Override
            public void onAddItem(ParcelModel parcel, UsableItem item) {
                if (item.getInfo().hasTemperatureEffect()) {
                    _items.add(item);
                }
            }
        });

        weatherModule.addObserver(new WeatherModuleObserver() {
            @Override
            public void onTemperatureChange(double temperature) {
            }
        });
    }

    @Override
    public void onGameStart(Game game) {
        _updateInterval = 10;
    }

    public void onGameUpdate(Game game, int tick) {
        if (roomModule != null) {
            roomModule.getRooms().forEach(room -> {
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
            });
        }
    }

    private void updateRoomTemperature(RoomModel room, double temperature, double connectionValue) {
        double diff = temperature - room.getTemperature();
        double ratio = 1;
        if (Math.abs(diff) > 5) ratio = 0.5;
        if (Math.abs(diff) > 10) ratio = 0.1;
        room.setTemperature(room.getTemperature() + (diff * connectionValue * ratio));
    }
}
