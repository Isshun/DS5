package org.smallbox.faraway.module.oxygen;

import org.smallbox.faraway.core.BindModule;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.game.module.room.model.RoomModel;
import org.smallbox.faraway.module.job.JobModule;
import org.smallbox.faraway.module.world.WorldModule;
import org.smallbox.faraway.module.world.WorldModuleObserver;
import org.smallbox.faraway.core.game.module.world.model.MapObjectModel;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.game.module.world.model.item.ItemModel;
import org.smallbox.faraway.module.room.RoomModule;
import org.smallbox.faraway.module.weather.WeatherModule;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Alex on 18/06/2015.
 */
public class OxygenModule extends GameModule {
    private @BindModule("base.module.job")
    JobModule _jobModule;

    private @BindModule("base.module.room")
    RoomModule _roomModule;

    private @BindModule("base.module.weather")
    WeatherModule _weatherModule;

    private @BindModule("base.module.world")
    WorldModule _worldModule;

    private double                  _oxygen;
    private List<ItemModel>         _items;

    public OxygenModule() {
        _updateInterval = 10;
    }

    public double getOxygen() { return _oxygen; }

    @Override
    protected void onGameStart(Game game) {

        _jobModule.addPriorityCheck(new CheckCharacterOxygen(this, _roomModule));

        _worldModule.addObserver(new WorldModuleObserver() {
            @Override
            public MapObjectModel putObject(ParcelModel parcel, ItemInfo itemInfo, int data, boolean complete) {
                return null;
            }

            @Override
            public void onAddParcel(ParcelModel parcel) {
            }

            @Override
            public void onAddItem(ParcelModel parcel, ItemModel item) {
                if (item.getInfo().effects != null && item.getInfo().effects.oxygen > 0) {
                    _items.add(item);
                }
            }

            @Override
            public void onRemoveItem(ParcelModel parcel, ItemModel item) {
                _items.remove(item);
            }
        });

        _oxygen = game.getPlanet().getOxygen();
        _items = _worldModule.getItems().stream().filter(item -> item.getInfo().effects != null && item.getInfo().effects.oxygen > 0).collect(Collectors.toList());
    }

    @Override
    protected void onGameUpdate(Game game, int tick) {
        if (_roomModule != null) {
            _roomModule.getRooms().forEach(r1 -> {
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
            room.setOxygen(_weatherModule.getOxygen());
        }
    }
}