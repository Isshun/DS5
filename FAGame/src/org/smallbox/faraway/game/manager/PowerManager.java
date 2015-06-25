package org.smallbox.faraway.game.manager;

import org.smallbox.faraway.game.model.item.ItemModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Alex on 18/06/2015.
 */
public class PowerManager extends BaseManager {
    private static final int    UPDATE_INTERVAL = 40;

    private List<ItemModel>     _items;
    private double              _produce;
    private double              _stored;

    public PowerManager() {
        _stored = 1000;
        _items = new ArrayList<>();
    }

    @Override
    protected void onUpdate(int tick) {
        if (tick % UPDATE_INTERVAL == 0) {
            Collections.shuffle(_items);
            double powerLeft = _stored;
            for (ItemModel item : _items) {
                if (powerLeft + item.getInfo().power > 0) {
                    powerLeft += item.getInfo().power;
                    item.setFunctional(true);
                } else {
                    item.setFunctional(false);
                }
            }
            _produce = powerLeft - _stored;
            _stored = powerLeft;
        }
    }

    @Override
    public void onAddItem(ItemModel item){
        if (item != null) {
            if (item.getInfo().power != 0) {
                _items.add(item);
            }
        }
    }

    @Override
    public void onRemoveItem(ItemModel item){
        _items.remove(item);
    }

    public double getProduce() {
        return _produce;
    }

    public double getStored() {
        return _stored;
    }
}
