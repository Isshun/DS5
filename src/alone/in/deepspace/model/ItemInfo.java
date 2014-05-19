package alone.in.deepspace.model;

public class ItemInfo {
	public static class ItemInfoCost {
		public int matter;
		public int power;
		public int o2;
	}

	public static class ItemInfoAction {
		public int tile;
		public int posX;
		public int posY;
	}

	public String 			name;
	public String 			inherits;
	public String 			label;
	public String 			category;
	public boolean 			isWalkable;
	public int 				width;
	public int 				height;
	public int 				light;
	public ItemInfoCost 	cost;
	public ItemInfoAction 	onAction;
	public boolean 			isStructure;
	public boolean 			isRessource;
	public boolean 			isUserItem;
	public int 				storage;
	public int 				spriteId;
	public String			fileName;
	public String 			packageName;
	
	public ItemInfo() {
		width = 1;
		height = 1;
	}
}