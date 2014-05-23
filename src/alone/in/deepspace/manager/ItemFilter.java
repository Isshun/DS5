package alone.in.deepspace.manager;

import alone.in.deepspace.model.ItemInfo;

public class ItemFilter {
	public boolean 	food;
	public boolean 	drink;
	public boolean 	energy;
	public boolean 	hapiness;
	public boolean 	relation;
	public boolean 	health;
	public boolean 	isFactory;
	public boolean 	isImmediate;
	public ItemInfo	matchingItem;
	public ItemInfo neededItem;
	
	public ItemFilter(boolean isFactory, boolean isImmediate) {
		this.isFactory = isFactory;
		this.isImmediate = isImmediate;
	}
}
