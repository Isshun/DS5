package org.smallbox.faraway.game.manager.world;

import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.manager.BaseManager;
import org.smallbox.faraway.game.model.item.ItemModel;
import org.smallbox.faraway.game.model.item.ParcelModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 03/07/2015.
 */
public class LightManager extends BaseManager {
    private List<ItemModel> _items = new ArrayList<>();

    public LightManager() {
        _updateInterval = 10;
    }

    @Override
    protected void onUpdate(int tick) {
        for (ItemModel item: _items) {
            update(item, true);
        }
    }

    private void update(ItemModel item, boolean isAdd) {
        int x = item.getX();
        int y = item.getY();

        for (int i = x - 8; i <= x + 8; i++) {
            for (int j = y - 8; j <= y + 8; j++) {
                ParcelModel parcel = Game.getWorldManager().getParcel(i, j);
                if (parcel != null) {
                    parcel.setLight(0);
                }
            }
        }

        if (isAdd) {
            for (int i = x - 8; i <= x + 8; i++) {
                for (int j = y - 8; j <= y + 8; j++) {
                    ParcelModel parcel = Game.getWorldManager().getParcel(i, j);
                    if (parcel != null) {
                        double distance = Math.max(1, Math.abs(x - i) + Math.abs(y - j));
                        parcel.setLight(Math.min(1, 0.4 + (16 - distance) / 20.0));
                    }
                }
            }
        }
    }

    @Override
    public void onAddItem(ItemModel item) {
        if (item.isLight()) {
            _items.add(item);
            update(item, true);
        }
    }

    @Override
    public void onRemoveItem(ItemModel item) {
        if (item.isLight()) {
            _items.remove(item);
            update(item, false);
        }
    }
}
