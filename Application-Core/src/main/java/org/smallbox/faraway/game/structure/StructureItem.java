package org.smallbox.faraway.game.structure;


import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.world.model.BuildableMapObject;

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
