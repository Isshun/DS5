package org.smallbox.faraway.module.oxygen;

import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.module.room.model.RoomModel;
import org.smallbox.faraway.core.world.model.ParcelModel;
import org.smallbox.faraway.module.item.ItemModule;
import org.smallbox.faraway.module.item.ItemModuleObserver;
import org.smallbox.faraway.module.item.UsableItem;
import org.smallbox.faraway.module.job.JobModule;
import org.smallbox.faraway.module.room.RoomModule;
import org.smallbox.faraway.module.weather.WeatherModule;
import org.smallbox.faraway.module.world.WorldModule;

import java.util.List;
import java.util.stream.Collectors;

public class OxygenModule extends GameModule {

    @BindModule
    private JobModule jobModule;

    @BindModule
    private RoomModule roomModule;

    @BindModule
    private WeatherModule weatherModule;

    @BindModule
    private WorldModule worldModule;

    @BindModule
    private ItemModule itemModule;

    private double                  _oxygen;
    private List<UsableItem>         _items;

    public OxygenModule() {
        _updateInterval = 10;
    }

    public double getOxygen() { return _oxygen; }

    @Override
    public void onGameCreate(Game game) {
        itemModule.addObserver(new ItemModuleObserver() {
            @Override
            public void onRemoveItem(ParcelModel parcel, UsableItem item) {
                _items.remove(item);
            }

            @Override
            public void onAddItem(ParcelModel parcel, UsableItem item) {
                if (item.getInfo().effects != null && item.getInfo().effects.oxygen > 0) {
                    _items.add(item);
                }
            }
        });
    }

    @Override
    public void onGameStart(Game game) {
        jobModule.addPriorityCheck(new CheckCharacterOxygen(this, roomModule));
        _oxygen = game.getPlanet().getOxygen();
        _items = itemModule.getItems().stream().filter(item -> item.getInfo().effects != null && item.getInfo().effects.oxygen > 0).collect(Collectors.toList());
    }

    @Override
    protected void onGameUpdate(Game game, int tick) {
        if (roomModule != null) {
            roomModule.getRooms().forEach(r1 -> {
                        // Mix oxygen with neighbors
                        r1.getConnections().forEach(connection -> {
                            RoomModel r2 = connection.getRoom();
                            int totalSize = r1.getSize() + r2.getSize();
                            double totalOxygen = (r1.getOxygen() * r1.getSize()) + (r2.getOxygen() * r2.getSize());
                            updateRoomPressure(r1, totalOxygen / totalSize, connection.getPermeability());
                            updateRoomPressure(r2, totalOxygen / totalSize, connection.getPermeability());
                        });

                        // Get oxygen from objects
                        r1.getParcels().forEach(parcel -> {
                            if (parcel.hasPlant()) {
                                addItemEffect(parcel.getRoom(), parcel.getPlant().getInfo().plant.oxygen);
                            }
                        });
                    }
            );
        }
    }

    private void addItemEffect(RoomModel room, double oxygen) {
        room.setOxygen(Math.min(1, (((room.getOxygen() * room.getSize()) + oxygen) / room.getSize())));
    }

    private void updateRoomPressure(RoomModel room, double oxygen, double permeability) {
        if (!room.isExterior()) {
            double diff = oxygen - room.getOxygen();
            double ratio = 1;
            if (Math.abs(diff) > 0.25) ratio = 0.5;
            if (Math.abs(diff) > 0.5) ratio = 0.1;
            room.setOxygen(room.getOxygen() + (diff * permeability * ratio));
        } else {
            room.setOxygen(weatherModule.getOxygen());
        }
    }
}