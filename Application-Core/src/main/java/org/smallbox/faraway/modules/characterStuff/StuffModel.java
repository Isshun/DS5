package org.smallbox.faraway.modules.characterStuff;

import org.smallbox.faraway.core.game.modelInfo.ItemInfo;

import java.util.ArrayList;
import java.util.List;

public class StuffModel {
    private List<ItemInfo> _items = new ArrayList<>();

    public void add(ItemInfo info) {
        _items.add(info);
    }

    public List<ItemInfo> getItemsInfo() {
        return _items;
    }
}
