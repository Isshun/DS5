package org.smallbox.faraway.core.game.module.world;

import org.smallbox.faraway.core.engine.module.ModuleBase;
import org.smallbox.faraway.core.engine.module.java.ModuleHelper;
import org.smallbox.faraway.core.engine.module.java.ModuleManager;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.module.room.RoomModule;
import org.smallbox.faraway.core.game.module.room.model.RoomConnectionModel;
import org.smallbox.faraway.core.game.module.room.model.RoomModel;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.game.module.world.model.item.ItemModel;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Alex on 18/06/2015.
 */
public class OxygenModule extends ModuleBase {
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
            roomModule.getRooms().forEach(r1 -> {
                        // Mix oxygen with neighbors
                        for (RoomConnectionModel roomConnection: r1.getConnections()) {
                            RoomModel r2 = roomConnection.getRoom();
                            int totalSize = r1.getSize() + r2.getSize();
                            double totalOxygen = (r1.getOxygen() * r1.getSize()) + (r2.getOxygen() * r2.getSize());
                            updateRoomPressure(r1, totalOxygen / totalSize, roomConnection.getPermeability());
                            updateRoomPressure(r2, totalOxygen / totalSize, roomConnection.getPermeability());
                        }

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
            room.setOxygen(ModuleHelper.getWeatherModule().getOxygen());
        }
    }

    @Override
    public void onAddItem(ItemModel item) {
        _items.remove(item);
    }

    @Override
    public void onRemoveItem(ParcelModel parcel, ItemModel item) {
        if (item.getInfo().effects != null && item.getInfo().effects.oxygen > 0) {
            _items.add(item);
        }
    }
}