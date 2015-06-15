package org.smallbox.faraway;

import org.smallbox.faraway.engine.util.Log;
import org.smallbox.faraway.manager.BaseManager;
import org.smallbox.faraway.manager.RoomManager;
import org.smallbox.faraway.manager.WorldManager;
import org.smallbox.faraway.model.item.ItemModel;
import org.smallbox.faraway.model.room.RoomModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

                    int totalHeatTarget = 0;
                    int totalColdTarget = 0;

                    for (ItemModel item : _items) {
                        if (item.getArea() != null && item.getArea().getRoom() == room) {
                            if (item.getInfo().effects.heatPotency != 0) {
                                totalHeatTarget += item.getTargetTemperature() * item.getInfo().effects.heatPotency;
                                temperatureInfo.heatPotency += item.getInfo().effects.heatPotency;
                            }
                            if (item.getInfo().effects.coldPotency != 0) {
                                temperatureInfo.targetCold += item.getTargetTemperature() * item.getInfo().effects.coldPotency;
                                temperatureInfo.coldPotency += item.getInfo().effects.coldPotency;
                            }
                        }
                    }

                    // Apply heat to room
                    if (temperatureInfo.heatPotency > 0) {
                        temperatureInfo.targetHeat = totalHeatTarget / temperatureInfo.heatPotency;
                        temperatureInfo.targetHeatTotal = temperatureInfo.targetHeat * room.getSize();
                        temperatureInfo.heatPotencyLeft = temperatureInfo.heatPotency;
                    }

                    // Apply cold to room
                    if (temperatureInfo.coldPotency > 0) {
                        temperatureInfo.targetCold = totalColdTarget / temperatureInfo.coldPotency;
                        temperatureInfo.targetColdTotal = temperatureInfo.targetHeat * room.getSize();
                        temperatureInfo.coldPotencyLeft = temperatureInfo.coldPotency;
                    }
                }
            }

            // Second pass
            // Diffuse temperature to neighborhood
            for (int i = 0; i < 20; i++) {
                for (RoomModel room : rooms) {
                    int roomSize = room.getSize();
                    if (!room.isExterior()) {
                        RoomModel.RoomTemperatureModel t1 = room.getTemperatureInfo();

                        // Diffuse temperature to neighbor
                        for (Map.Entry<RoomModel, Integer> entry : room.getNeighborhoods().entrySet()) {
                            RoomModel.RoomTemperatureModel t2 = entry.getKey().getTemperatureInfo();
                            double neighborRatio = (double) entry.getValue() / room.getSize();
                            t1.temperatureTotal += (t2.temperatureTotal / entry.getKey().getSize() - t1.temperatureTotal / roomSize) * neighborRatio;
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
