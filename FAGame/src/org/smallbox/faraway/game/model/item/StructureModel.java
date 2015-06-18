package org.smallbox.faraway.game.model.item;


public class StructureModel extends MapObjectModel {

	public StructureModel(ItemInfo info, int id) {
		super(info, id);
	}

	public StructureModel(ItemInfo info) {
		super(info);
	}

	// TODO: item
	public boolean roomCanBeSet() {
		return _info.name.equals("base.floor") || _info.name.equals("base.ground");
	}

	public boolean isHull() {
		return getName().equals("base.hull");
	}

	public boolean isGround() {
		return getName().equals("base.ground");
	}
}
