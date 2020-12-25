package org.smallbox.faraway.modules.temperature;

import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.modules.item.ItemModule;
import org.smallbox.faraway.modules.room.RoomModule;
import org.smallbox.faraway.modules.room.model.RoomConnectionModel;
import org.smallbox.faraway.modules.room.model.RoomModel;
import org.smallbox.faraway.modules.weather.WeatherModule;
import org.smallbox.faraway.modules.weather.WeatherModuleObserver;
import org.smallbox.faraway.modules.world.WorldModule;

@GameObject
public class TemperatureModule extends GameModule {

    @Inject
    private WorldModule worldModule;

    @Inject
    private WeatherModule weatherModule;

    @Inject
    private RoomModule roomModule;

    @Inject
    private ItemModule itemModule;

    @Override
    public void onGameCreate(Game game) {
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

    @Override
    public void onModuleUpdate(Game game) {
        if (roomModule != null) {
            roomModule.getRooms().forEach(room -> {
                if (room.isExterior()) {
                    room.setTemperature(weatherModule.getTemperatureFloor(room.getFloor()));
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
