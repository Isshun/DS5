package alone.in.deepspace.model;

import java.util.ArrayList;
import java.util.List;

import alone.in.deepspace.manager.ItemFilter;

public class ItemInfo {
	public static class ItemInfoCost {
		public int matter;
		public int power;
		public int o2;
	}

	public static class ItemInfoAction {
		public int 					duration;
		public ItemInfoProduce		produce;
		public ItemInfo 			itemProduce;
		public ItemInfoPractice 	practice;
		public ItemInfoEffects		effects;
		public List<ItemInfoSlot>	slots;
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
	public List<String>			craftedFrom;
	public boolean 				isWalkable;
	public int 					width;
	public int 					height;
	public int 					light;
	public ItemInfoCost 		cost;
	public ItemInfoAction 		onAction;
	public ItemInfoRessource	onGather;
	public ItemInfoRessource	onMine;
	public boolean 				isStructure;
	public boolean 				isRessource;
	public boolean 				isConsomable;
	public boolean 				isUserItem;
	public boolean 				isFood;
	public int 					storage;
	public int 					spriteId;
	public String				fileName;
	public String 				packageName;
	public List<ItemInfo> 		craftedFromItems;
	
	public ItemInfo() {
		width = 1;
		height = 1;
	}
	
	public boolean matchFilter(ItemFilter filter) {

		// Item immediate effect
		if (filter.isImmediate && matchFilter(onAction.effects, filter)) {
			filter.matchingItem = this;
			return true;
		}

		// Item produce
		if (filter.isFactory && onAction != null && onAction.itemProduce != null) {
			List<ItemInfo> itemProduces = new ArrayList<ItemInfo>();
			itemProduces.add(onAction.itemProduce);
			for (ItemInfo itemProduce: itemProduces) {
				if (itemProduce != null && itemProduce.onAction != null && matchFilter(itemProduce.onAction.effects, filter)) {
					filter.matchingItem = itemProduce;
					return true;
				}
			}
		}
		
		return false;
	}

	private boolean matchFilter(ItemInfoEffects effects, ItemFilter filter) {
		if (effects != null) {
			if (filter.drink && effects.drink > 0) { return true; }
			if (filter.energy && effects.energy > 0) { return true; }
			if (filter.food && effects.food > 0) { return true; }
			if (filter.hapiness && effects.hapiness > 0) { return true; }
			if (filter.health && effects.health > 0) { return true; }
			if (filter.relation && effects.relation > 0) { return true; }
		}
		return false;
	}

}