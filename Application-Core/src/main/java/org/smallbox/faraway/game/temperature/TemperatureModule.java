package org.smallbox.faraway.game.temperature;

import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotation.callback.applicationEvent.OnInit;
import org.smallbox.faraway.core.dependencyInjector.annotation.callback.gameEvent.OnGameLongUpdate;
import org.smallbox.faraway.core.dependencyInjector.annotation.callback.gameEvent.OnGameUpdate;
import org.smallbox.faraway.core.module.SuperGameModule;
import org.smallbox.faraway.game.item.ItemModule;
import org.smallbox.faraway.game.room.RoomModule;
import org.smallbox.faraway.game.room.model.RoomModel;
import org.smallbox.faraway.game.weather.WeatherModule;
import org.smallbox.faraway.game.weather.WorldTemperatureModule;
import org.smallbox.faraway.game.world.Parcel;
import org.smallbox.faraway.game.world.WorldModule;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@GameObject
public class TemperatureModule extends SuperGameModule {
    private final static int ELEMENTS_BY_UPDATE = 100000;

    @Inject private WorldTemperatureModule worldTemperatureModule;
    @Inject private WorldModule worldModule;
    @Inject private WeatherModule weatherModule;
    @Inject private RoomModule roomModule;
    @Inject private ItemModule itemModule;

    private Map<Parcel, Double> temperatureByParcel = new ConcurrentHashMap<>();
    private Queue<Parcel> parcelQueue = new ConcurrentLinkedQueue<>();

    @OnInit
    public void onInit() {
//        weatherModule.addObserver(new WeatherModuleObserver() {
//            @Override
//            public void onTemperatureChange(double temperature) {
//            }
//        });
    }

    @OnGameLongUpdate
    public void onGameLongUpdate() {
        if (parcelQueue.isEmpty()) {
            parcelQueue.addAll(worldModule.getAll());
        }

        for (int i = 0; i < ELEMENTS_BY_UPDATE; i++) {
            if (!parcelQueue.isEmpty()) {
                temperatureByParcel.put(parcelQueue.poll(), worldTemperatureModule.getTemperature());
            }
        }
    }

    public double getTemperature(Parcel parcel) {
        return temperatureByParcel.getOrDefault(parcel, worldTemperatureModule.getTemperature());
    }

    @OnGameUpdate
    public void onGameUpdate() {
//        if (roomModule != null) {
//            roomModule.getAll().forEach(room -> {
//                if (room.isExterior()) {
//                    room.setTemperature(weatherModule.getTemperatureFloor(room.getFloor()));
//                } else {
//                    // Mix temperature with neighbors
//                    for (RoomConnectionModel roomConnection: room.getConnections()) {
//                        int totalSize = room.getSize() + roomConnection.getRoom().getSize();
//                        double totalTemperature = (room.getTemperature() * room.getSize()) + (roomConnection.getRoom().getTemperature() * roomConnection.getRoom().getSize());
//                        updateRoomTemperature(room, totalTemperature / totalSize, roomConnection.getPermeability());
//                        updateRoomTemperature(roomConnection.getRoom(), totalTemperature / totalSize, roomConnection.getPermeability());
//                    }
//
//                    // Get temperature from objects
//                    room.getParcels().forEach(parcel -> {
//                    });
//                }
//            });
//        }
    }

    private void updateRoomTemperature(RoomModel room, double temperature, double connectionValue) {
        double diff = temperature - room.getTemperature();
        double ratio = 1;
        if (Math.abs(diff) > 5) ratio = 0.5;
        if (Math.abs(diff) > 10) ratio = 0.1;
        room.setTemperature(room.getTemperature() + (diff * connectionValue * ratio));
    }
}
