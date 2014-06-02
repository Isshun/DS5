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
		return getName().equals("base.floor");// == BaseItem.Type.STRUCTURE_FLOOR || _type == BaseItem.Type.STRUCTURE_GREENHOUSE;
	}

	public boolean isHull() {
		return getName().equals("base.hull");
	}

}
