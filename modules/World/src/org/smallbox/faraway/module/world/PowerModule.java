package org.smallbox.faraway.module.world;

import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.module.world.model.item.ItemModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Alex on 18/06/2015.
 */
public class PowerModule extends GameModule {
    private static final int    UPDATE_INTERVAL = 40;

    private List<ItemModel>     _items;
    private double              _produce;
    private double              _stored;
    private double              _maxStorage;

    @Override
    protected void onLoaded(Game game) {
        _stored = 1000;
        _maxStorage = 3500;
        _items = new ArrayList<>();
    }

    @Override
    protected boolean loadOnStart() {
        return true;
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
            _stored = Math.min(_maxStorage, powerLeft);
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
