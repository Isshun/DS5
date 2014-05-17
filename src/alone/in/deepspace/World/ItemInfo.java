package alone.in.deepspace.World;

public class ItemInfo {
	public static class ItemInfoCost {
		public int matter;
		public int power;
		public int o2;
	}

	public String 		name;
	public String 		inherits;
	public String 		label;
	public boolean 		isWalkable;
	public int 			width;
	public int 			height;
	public int 			light;
	public ItemInfoCost cost;
	public boolean 		isStructure;
	public boolean 		isRessource;
	public boolean 		isUserItem;
	public int 			storage;
	
}