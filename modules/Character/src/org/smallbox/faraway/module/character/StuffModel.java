package org.smallbox.faraway.module.character;

import org.smallbox.faraway.core.game.modelInfo.ItemInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 02/09/2015.
 */
public class StuffModel {
    private List<ItemInfo> _items = new ArrayList<>();

    public void add(ItemInfo info) {
        _items.add(info);
    }

    public List<ItemInfo> getItemsInfo() {
        return _items;
    }
}
