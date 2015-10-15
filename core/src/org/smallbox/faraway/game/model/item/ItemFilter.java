package org.smallbox.faraway.game.model.item;


public class ItemFilter {
	public boolean 	effectFood;
	public boolean 	effectDrink;
	public boolean 	effectEnergy;
	public boolean 	effectHappiness;
	public boolean 	effectRelation;
	public boolean 	effectHealth;
	public boolean 	effectEntertainment;
	public boolean 	lookingForFactory;
	public boolean 	lookingForItem;
	public ItemInfo	itemMatched;
	public ItemInfo itemNeeded;
	public Boolean 	needFreeSlot;
	public boolean isFree;
	public boolean needItem;
	public boolean needConsumable;

	private ItemFilter(boolean isFactory, boolean isImmediate) {
		this.lookingForFactory = isFactory;
		this.lookingForItem = isImmediate;
	}

	public static ItemFilter createConsomableFilter() {
		ItemFilter filter = new ItemFilter(false, true);
		filter.needConsumable = true;
		filter.isFree = true;
		filter.needFreeSlot = false;
		return filter;
	}

	public static ItemFilter createConsomableFilter(ItemInfo neededItemInfo) {
		ItemFilter filter = new ItemFilter(false, true);
		filter.needConsumable = true;
		filter.needFreeSlot = false;
		filter.isFree = true;
		filter.itemNeeded = neededItemInfo;
		return filter;
	}
	
	public static ItemFilter createFactoryFilter() {
		ItemFilter filter = new ItemFilter(true, false);
		filter.needFreeSlot = true;
		return filter;
	}
	
	public static ItemFilter createFactoryFilter(ItemInfo neededItemInfo) {
		ItemFilter filter = new ItemFilter(true, false);
		filter.needFreeSlot = true;
		filter.itemNeeded = neededItemInfo;
		return filter;
	}

	public static ItemFilter createUsableFilter() {
		ItemFilter filter = new ItemFilter(false, true);
		filter.needFreeSlot = true;
		return filter;
	}

	public static ItemFilter createUsableFilter(ItemInfo neededItemInfo) {
		ItemFilter filter = new ItemFilter(false, true);
		filter.needFreeSlot = true;
		filter.itemNeeded = neededItemInfo;
		return filter;
	}

	public static ItemFilter createItemFilter() {
		ItemFilter filter = new ItemFilter(false, true);
		filter.needFreeSlot = true;
		filter.needItem = true;
		return filter;
	}
}
