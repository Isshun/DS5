package org.smallbox.faraway.core.game.model.item;


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
        return getName().equals("base.floor");
    }
}
