package org.smallbox.faraway.module.world;

import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.GameObserver;
import org.smallbox.faraway.game.model.GameData;
import org.smallbox.faraway.game.model.item.ItemModel;
import org.smallbox.faraway.game.model.room.NeighborModel;
import org.smallbox.faraway.game.model.room.RoomModel;
import org.smallbox.faraway.game.module.GameModule;
import org.smallbox.faraway.game.module.ModuleManager;
import org.smallbox.faraway.game.module.base.RoomModule;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 13/06/2015.
 */
public class TemperatureModule extends GameModule implements GameObserver {
    private RoomModule _roomModule;
    private List<ItemModel>     _items = new ArrayList<>();
    private double              _temperature;
    private double              _temperatureTarget;
    private double              _temperatureOffset;

    @Override
    public void onLoaded() {
        _temperature = _temperatureTarget = Game.getInstance().getRegion().getInfo().temperature[1];
        _roomModule = (RoomModule) ModuleManager.getInstance().getModule(RoomModule.class);
    }

    @Override
    protected boolean loadOnStart() {
        return GameData.config.manager.temperature;
    }

    public void     setTemperature(double temperature) { _temperatureTarget = temperature; }
    public void     setTemperatureOffset(int temperatureOffset) { _temperatureOffset = temperatureOffset; }
    public void     increaseTemperature() { _temperatureTarget++; }
    public void     decreaseTemperature() { _temperatureTarget--; }
    public void     normalize() { _temperature = _temperatureTarget; }
    public double   getTemperature() { return _temperature; }
    public double   getTemperatureTarget() { return _temperatureTarget; }
    public double   getTemperatureOffset() { return _temperatureOffset; }

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
        double change = ((_temperatureTarget + _temperatureOffset) - _temperature) / 100;
        if (change > -0.001 && change < 0.001) {
            _temperature = _temperatureTarget + _temperatureOffset;
        } else if (change > -0.01 && change < 0.01) {
            _temperature += change < 0 ? -0.01 : 0.01;
        } else {
            _temperature += change;
        }

        if (tick % 25 == 0) {
            printDebug("update temperature (" + _temperature + ")");

            // Check heat / cold effects and room heatPotency then apply to room
            pass1();

            // Diffuse temperature to neighborhood
            pass2();

            // Set potency use on each items
            pass3();

            // Set final room temperature
            for (RoomModel room : _roomModule.getRoomList()) {
                room.getTemperatureInfo().temperature = Math.round(room.getTemperatureInfo().temperatureTotal / room.getSize());
            }
        }
    }

    private void pass3() {
        for (RoomModel room : _roomModule.getRoomList()) {
            if (!room.getHeatItems().isEmpty()) {
                int heatPotencyUsePerItem = (room.getTemperatureInfo().heatPotency - room.getTemperatureInfo().heatPotencyLeft) / room.getHeatItems().size();
                room.getHeatItems().forEach(item -> item.setPotencyUse(heatPotencyUsePerItem));
            }

            if (!room.getColdItems().isEmpty()) {
                int coldPotencyUsePerItem = (room.getTemperatureInfo().coldPotency - room.getTemperatureInfo().coldPotencyLeft) / room.getColdItems().size();
                room.getColdItems().forEach(item -> item.setPotencyUse(coldPotencyUsePerItem));
            }
        }
    }

    private void pass2() {
        for (int i = 0; i < 16; i++) {
            for (RoomModel room : _roomModule.getRoomList()) {
                int roomSize = room.getSize();
                if (!room.isExterior()) {
                    RoomModel.RoomTemperatureModel t1 = room.getTemperatureInfo();

                    // Diffuse temperature to room
                    for (NeighborModel neighbor : room.getNeighbors()) {
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
    }

    private void pass1() {
        for (RoomModel room : _roomModule.getRoomList()) {
            RoomModel.RoomTemperatureModel temperatureInfo = room.getTemperatureInfo();
            temperatureInfo.temperature = _temperature;
            temperatureInfo.temperatureTotal = _temperature * room.getSize();

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
