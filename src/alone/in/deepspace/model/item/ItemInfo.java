package alone.in.deepspace.model.item;

import java.util.ArrayList;
import java.util.List;


public class ItemInfo {
	public static class ItemInfoCost {
		public int matter;
		public int power;
		public int o2;
	}

	public static class ItemInfoAction {
		public int 					duration;
		public int 					storage;
		public List<String>			produce;
		public List<ItemInfo>		itemsProduce;
		public ItemInfoPractice 	practice;
		public ItemInfoEffects		effects;
		public List<ItemInfoSlot>	slots;
		public ArrayList<ItemInfo> 	itemAccept;
	}

	public static class ItemInfoRessource {
		public String 	produce;
		public ItemInfo itemProduce;
		public int		mature;
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

	public static class ItemInfoEffects {
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
	public String 				type;
	public List<String>			craftedFrom;
	public int					craftedQuantitfy;
	public boolean 				isWalkable;
	public int 					frames;
	public int 					framesInterval;
	public int 					width;
	public int 					height;
	public int 					light;
	public ItemInfoCost 		cost;
	public ItemInfoAction 		onAction;
	public ItemInfoRessource	onGather;
	public ItemInfoRessource	onMine;
	public boolean 				isStructure;
	public boolean 				isResource;
	public boolean 				isConsomable;
	public boolean 				isUserItem;
	public boolean 				isFood;
	public int 					storage;
	public int 					spriteId;
	public String				fileName;
	public String 				packageName;
	public List<ItemInfo> 		craftedFromItems;
	public boolean 				isFactory;
	public boolean 				isStorage;
	public boolean 				isDrink;
	
	public ItemInfo() {
		width = 1;
		height = 1;
		isWalkable = true;
	}
	
	boolean matchFilter(ItemInfoEffects effects, ItemFilter filter) {
		if (effects != null) {
			if (filter.effectDrink && effects.drink > 0) { return true; }
			if (filter.effectEnergy && effects.energy > 0) { return true; }
			if (filter.effectFood && effects.food > 0) { return true; }
			if (filter.effectHapiness && effects.hapiness > 0) { return true; }
			if (filter.effectHealth && effects.health > 0) { return true; }
			if (filter.effectRelation && effects.relation > 0) { return true; }
		}
		return false;
	}

}