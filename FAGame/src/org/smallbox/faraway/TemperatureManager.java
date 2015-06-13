package org.smallbox.faraway;

import org.smallbox.faraway.manager.RoomManager;
import org.smallbox.faraway.model.item.ItemModel;
import org.smallbox.faraway.model.room.RoomModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 13/06/2015.
 */
public class TemperatureManager implements WorldObserver {
    private List<ItemModel> _items = new ArrayList<>();

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

    public void update(int exteriorTemperature, List<RoomModel> rooms) {
        for (RoomModel room: rooms) {
            room.setTemperature(exteriorTemperature);

            // Check heat / cold effects
            if (!room.isExterior()) {
                int totalCold = 0;
                int totalColdPotency = 0;
                int totalHeat = 0;
                int totalHeatPotency = 0;

                for (ItemModel item : _items) {
                    if (item.getArea() != null && item.getArea().getRoom() == room) {
                        if (item.getInfo().effects.heatPotency != 0) {
                            totalHeat += item.getTargetTemperature() * item.getInfo().effects.heatPotency;
                            totalHeatPotency += item.getInfo().effects.heatPotency;
                        }
                        if (item.getInfo().effects.coldPotency != 0) {
                            totalCold += item.getTargetTemperature() * item.getInfo().effects.coldPotency;
                            totalColdPotency += item.getInfo().effects.coldPotency;
                        }
                    }
                }

                // Heat room
                if (totalHeatPotency > 0) {
                    int targetHeat = totalHeat / totalHeatPotency;
                    if (targetHeat > room.getTemperature()) {
                        double diff = targetHeat - room.getTemperature();
                        if (room.getSize() > totalHeatPotency) {
                            room.setTemperature(room.getTemperature() + (diff * totalHeatPotency / room.getSize()));
                        } else {
                            room.setTemperature(targetHeat);
                        }
                    }
                }
            }
        }
    }
}
