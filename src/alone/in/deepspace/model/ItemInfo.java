package alone.in.deepspace.model;

import java.util.List;

public class ItemInfo {
	public static class ItemInfoCost {
		public int matter;
		public int power;
		public int o2;
	}

	public static class ItemInfoAction {
		public int 				tile;
		public int 				posX;
		public int 				posY;
		public int 				duration;
		public ItemInfoProduce	produce;
		public ItemInfo 		itemProduce;
		public ItemInfoPractice practice;
		public ItemInfoNeeds	effects;
	}

	public static class ItemInfoRessource {
		public String 	produce;
		public ItemInfo itemProduce;
	}

	public static class ItemInfoProduce {
		public String 	item;
		public int 		quantity;
	}

	public static class ItemInfoSlot {
		public int 		x;
		public int 		y;
	}

	public static class ItemInfoPractice {
		public int		cooking;
	}

	public static class ItemInfoNeeds {
		public int		food;
		public int 		drink;
		public int 		energy;
		public int 		hapiness;
		public int 		health;
		public int 		relation;
	}

	public String 				name;
	public String 				inherits;
	public String 				label;
	public String 				category;
	public List<String>			craftedFrom;
	public boolean 				isWalkable;
	public int 					width;
	public int 					height;
	public int 					light;
	public List<ItemInfoSlot>	slots;
	public ItemInfoCost 		cost;
	public ItemInfoAction 		onAction;
	public ItemInfoRessource	onGather;
	public ItemInfoRessource	onMine;
	public ItemInfoNeeds		onConsumption;
	public boolean 				isStructure;
	public boolean 				isRessource;
	public boolean 				isConsomable;
	public boolean 				isUserItem;
	public int 					storage;
	public int 					spriteId;
	public String				fileName;
	public String 				packageName;
	public List<ItemInfo> 		craftedFromItems;
	
	public ItemInfo() {
		width = 1;
		height = 1;
	}
}