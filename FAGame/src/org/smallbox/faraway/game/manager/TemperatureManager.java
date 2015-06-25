package org.smallbox.faraway.game.manager;

import org.smallbox.faraway.game.GameObserver;
import org.smallbox.faraway.game.model.item.ItemModel;
import org.smallbox.faraway.game.model.room.NeighborModel;
import org.smallbox.faraway.game.model.room.RoomModel;
import org.smallbox.faraway.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 13/06/2015.
 */
public class TemperatureManager extends BaseManager implements GameObserver {
    private static final int    UPDATE_INTERVAL = 25;

    private final WorldManager  _worldManager;
    private final RoomManager   _roomManager;
    private List<ItemModel>     _items;
    private double              _exteriorTemperature;
    private int                 _count;

    public TemperatureManager(WorldManager worldManager, RoomManager roomManager) {
        _worldManager = worldManager;
        _roomManager = roomManager;
        _items = new ArrayList<>();
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
        if (tick % UPDATE_INTERVAL == 0) {
            List<RoomModel> rooms = _roomManager.getRoomList();
            int exteriorTemperature = _worldManager.getTemperature();

            _exteriorTemperature += (exteriorTemperature - _exteriorTemperature) / 10;

            Log.debug("update temperature (" + _exteriorTemperature + ")");

            // First pass
            // Check heat / cold effects and room heatPotency then apply to room
            for (RoomModel room : rooms) {
                RoomModel.RoomTemperatureModel temperatureInfo = room.getTemperatureInfo();
                temperatureInfo.temperature = _exteriorTemperature;
                temperatureInfo.temperatureTotal = _exteriorTemperature * room.getSize();

                if (room.isExterior()) {
                    temperatureInfo.heatPotency = 1000;
                    temperatureInfo.coldPotency = 1000;
                } else {
                    temperatureInfo.heatPotency = 0;
                    temperatureInfo.heatPotencyLeft = 0;
                    temperatureInfo.coldPotency = 0;
                    temperatureInfo.coldPotencyLeft = 0;

                    // Apply heat to room
                    int totalHeatTarget = 0;
                    for (ItemModel item : room.getHeatItems()) {
                        totalHeatTarget += item.getTargetTemperature() * item.getInfo().effects.heatPotency;
                        temperatureInfo.heatPotency += item.getInfo().effects.heatPotency;
                    }
                    if (temperatureInfo.heatPotency > 0) {
                        temperatureInfo.targetHeat = totalHeatTarget / temperatureInfo.heatPotency;
                        temperatureInfo.targetHeatTotal = temperatureInfo.targetHeat * room.getSize();
                        temperatureInfo.heatPotencyLeft = temperatureInfo.heatPotency;
                    }

                    // Apply cold to room
                    int totalColdTarget = 0;
                    for (ItemModel item : room.getColdItems()) {
                        temperatureInfo.targetCold += item.getTargetTemperature() * item.getInfo().effects.coldPotency;
                        temperatureInfo.coldPotency += item.getInfo().effects.coldPotency;
                    }
                    if (temperatureInfo.coldPotency > 0) {
                        temperatureInfo.targetCold = totalColdTarget / temperatureInfo.coldPotency;
                        temperatureInfo.targetColdTotal = temperatureInfo.targetHeat * room.getSize();
                        temperatureInfo.coldPotencyLeft = temperatureInfo.coldPotency;
                    }
                }
            }

            // Second pass
            // Diffuse temperature to neighborhood
            for (int i = 0; i < 16; i++) {
                for (RoomModel room : rooms) {
                    int roomSize = room.getSize();
                    if (!room.isExterior()) {
                        RoomModel.RoomTemperatureModel t1 = room.getTemperatureInfo();

                        // Diffuse temperature to room
                        for (NeighborModel neighbor: room.getNeighbors()) {
                            RoomModel.RoomTemperatureModel t2 = neighbor.room.getTemperatureInfo();
                            double neighborRatio = (double) neighbor.parcels.size() * 2 / room.getSize();
                            t1.temperatureTotal += (t2.temperatureTotal / neighbor.room.getSize() - t1.temperatureTotal / roomSize) * neighborRatio;
                        }

                        // Increase temperature of the room
                        if (t1.targetHeatTotal > t1.temperatureTotal) {
                            double change = (t1.targetHeatTotal - t1.temperatureTotal) / roomSize / 2;
                            double value = change * roomSize;
                            if (t1.heatPotencyLeft < value) {
                                value = t1.heatPotencyLeft;
                            }
                            t1.heatPotencyLeft -= value;
                            t1.temperatureTotal += value * 2;
                        }
                    }
                }
            }

            // Set potency use on each items
            for (RoomModel room: rooms) {
                if (!room.getHeatItems().isEmpty()) {
                    int heatPotencyUsePerItem = (room.getTemperatureInfo().heatPotency - room.getTemperatureInfo().heatPotencyLeft) / room.getHeatItems().size();
                    room.getHeatItems().forEach(item -> item.setPotencyUse(heatPotencyUsePerItem));
                }

                if (!room.getColdItems().isEmpty()) {
                    int coldPotencyUsePerItem = (room.getTemperatureInfo().coldPotency - room.getTemperatureInfo().coldPotencyLeft) / room.getColdItems().size();
                    room.getColdItems().forEach(item -> item.setPotencyUse(coldPotencyUsePerItem));
                }
            }

            // Set final room temperature
            for (RoomModel room : rooms) {
                room.getTemperatureInfo().temperature = Math.round(room.getTemperatureInfo().temperatureTotal / room.getSize());
            }
        }
//
//        // Second pass: apply heat / cold to roms
//        for (RoomModel room: rooms) {
//            RoomModel.RoomTemperatureModel temperatureInfo = room.getTemperatureInfo();
//
////            // Apply heat to room
////            if (temperatureInfo.heatPotency > 0) {
////                temperatureInfo.targetHeat = totalHeat / temperatureInfo.heatPotency;
////                if (temperatureInfo.targetHeat > room.getTemperature()) {
////                    double diff = temperatureInfo.targetHeat - room.getTemperature();
////                    if (room.getSize() > temperatureInfo.heatPotency) {
////                        temperatureInfo.directTemperature = room.getTemperature() + (diff * temperatureInfo.heatPotency / room.getSize());
////                    } else {
////                        temperatureInfo.directTemperature = temperatureInfo.targetHeat;
////                    }
////                }
////            }
//        }
    }
}
