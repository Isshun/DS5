package alone.in.deepspace.World;

public class StructureItem extends BaseItem {

	public StructureItem(Type type, int id) {
		super(type, id);
		// TODO Auto-generated constructor stub
	}

	public StructureItem(Type type) {
		super(type);
	}

	public boolean roomCanBeSet() {
		return _type == BaseItem.Type.STRUCTURE_FLOOR || _type == BaseItem.Type.STRUCTURE_GREENHOUSE;
	}

	public boolean isFloor() { return _type == BaseItem.Type.STRUCTURE_FLOOR; }

}
