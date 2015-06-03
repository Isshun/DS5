package org.smallbox.faraway.model.item;

import java.util.ArrayList;
import java.util.List;

public class ItemInfo {

	public boolean hasCraftAction() {
		if (actions != null) {
			for (ItemInfoAction action : actions) {
				if ("craft".equals(action.type) || "cook".equals(action.type)) {
					return true;
				}
			}
		}
		return false;
	}

	public static class ItemInfoCost {
		public int matter;
		public int power;
		public int o2;
	}

	public static class ItemInfoAction {
		public int 					storage;
		public String 				type;
		public String 				label;
		public List<String> 		products;
		public List<ItemInfo> 		productsItem;
		public ItemInfoEffects		effects;
		public ArrayList<ItemInfo> 	itemAccept;
		public int 					mature;
		public int                  cost;
		public String 				name;
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
	public List<int[]>			slots;
	public List<String>         receipts;
	public int					craftedQuantitfy;
	public boolean 				isWalkable;
	public int 					frames;
	public int 					framesInterval;
	public int 					width;
	public int 					height;
	public int 					light;
	public ItemInfoCost 		cost;
	public List<ItemInfoAction> actions;
	public boolean 				isStructure;
	public boolean 				isResource;
	public boolean 				isConsomable;
	public boolean 				isUserItem;
	public boolean 				isFood;
	public boolean 				isToy;
	public boolean 				isBed;
	public int 					storage;
	public int 					spriteId;
	public String				fileName;
	public String 				packageName;
	public List<ItemInfo> 		craftedFromItems;
	public boolean 				isFactory;
	public boolean 				isStorage;
	public boolean 				isDrink;
	public boolean 				isSleeping;
	public boolean 				isStack;
	public int 					maxHealth;

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