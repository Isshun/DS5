package org.smallbox.faraway.core.module.world.model;


import org.smallbox.faraway.core.game.modelInfo.ItemInfo;

public class StructureModel extends BuildableMapObject {

    public StructureModel(ItemInfo info, int id) {
        super(info, id);
    }

    public StructureModel(ItemInfo info) {
        super(info);
    }

    public boolean isHull() {
        return getName().equals("base.hull");
    }

    public boolean isFloor() {
        return _info.isFloor;
    }
}
