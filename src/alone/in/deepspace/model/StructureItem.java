package alone.in.deepspace.model;

public class StructureItem extends BaseItem {

	private int _tile;

	public StructureItem(ItemInfo info, int id) {
		super(info, id);
		// TODO Auto-generated constructor stub
	}

	public StructureItem(ItemInfo info) {
		super(info);
	}

	// TODO: item
	public boolean roomCanBeSet() {
		return getName().equals("base.floor");// == BaseItem.Type.STRUCTURE_FLOOR || _type == BaseItem.Type.STRUCTURE_GREENHOUSE;
	}

}
