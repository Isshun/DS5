package org.smallbox.faraway.core.module.world.model;


import org.smallbox.faraway.core.game.modelInfo.ItemInfo;

public class StructureItem extends BuildableMapObject {

    public StructureItem(ItemInfo info, int id) {
        super(info, id);
    }

    public StructureItem(ItemInfo info) {
        super(info);
    }

    public boolean isFloor() {
        return _info.isFloor;
    }
}
