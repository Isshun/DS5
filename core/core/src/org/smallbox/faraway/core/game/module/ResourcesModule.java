package org.smallbox.faraway.core.game.module;

import org.smallbox.faraway.core.game.model.item.ConsumableModel;
import org.smallbox.faraway.core.game.model.item.ItemInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alex on 09/10/2015.
 */
public class ResourcesModule extends GameModule {
    private Map<ItemInfo, Integer>  _resources = new HashMap<>();

    @Override
    protected void onLoaded() {
    }

    @Override
    protected void onUpdate(int tick) {
    }

    @Override
    public void onAddConsumable(ConsumableModel consumable) {
        int quantity = _resources.containsKey(consumable.getInfo()) ? _resources.get(consumable.getInfo()) : 0;
        _resources.put(consumable.getInfo(), quantity + consumable.getQuantity());
    }

    @Override
    public void onRemoveConsumable(ConsumableModel consumable) {
        int quantity = _resources.containsKey(consumable.getInfo()) ? _resources.get(consumable.getInfo()) : 0;
        _resources.put(consumable.getInfo(), quantity - consumable.getQuantity());
    }
}
