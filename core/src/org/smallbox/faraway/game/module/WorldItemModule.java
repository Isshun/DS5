package org.smallbox.faraway.game.module;

import org.smallbox.faraway.game.model.item.ItemInfo;
import org.smallbox.faraway.game.model.item.ItemModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 18/06/2015.
 */
public class WorldItemModule extends GameModule {
    private List<ItemModel> _items;

    @Override
    protected void onLoaded() {
        _items = new ArrayList<>();
    }

    @Override
    protected void onUpdate(int tick) {
        for (ItemModel item: _items) {
            if (item.isActive() && item.isFunctional()) {
                ItemInfo.ItemInfoEffects effects = item.getInfo().effects;
                if (effects != null) {

                    // Check oxygen
                    if (effects.oxygen != 0) {
                        if (item.getParcel() != null && item.getParcel().getRoom() != null) {
                            item.getParcel().getRoom().addOxygen(effects.oxygen);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onAddItem(ItemModel item){
        if (item != null) {
            _items.add(item);
        }
    }

    @Override
    public void onRemoveItem(ItemModel item){
        _items.remove(item);
    }
}
