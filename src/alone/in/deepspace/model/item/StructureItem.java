package alone.in.deepspace.model.item;


public class StructureItem extends ItemBase {

	public StructureItem(ItemInfo info, int id) {
		super(info, id);
	}

	public StructureItem(ItemInfo info) {
		super(info);
	}

	// TODO: item
	public boolean roomCanBeSet() {
		return _info.name.equals("base.floor") || _info.name.equals("base.ground");
	}

	public boolean isHull() {
		return getName().equals("base.hull");
	}

}
