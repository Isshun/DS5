package alone.in.deepspace.Models;

public class StructureItem extends BaseItem {

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
