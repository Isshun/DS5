package alone.in.deepspace.model.item;


public class ItemFilter {
	public boolean 	effectFood;
	public boolean 	effectDrink;
	public boolean 	effectEnergy;
	public boolean 	effectHapiness;
	public boolean 	effectRelation;
	public boolean 	effectHealth;
	public boolean 	lookingForFactory;
	public boolean 	lookingForItem;
	public ItemInfo	itemMatched;
	public ItemInfo itemNeeded;
	public boolean 	needFreeSlot;
	
	public ItemFilter(boolean isFactory, boolean isImmediate) {
		this.lookingForFactory = isFactory;
		this.lookingForItem = isImmediate;
		this.needFreeSlot = true;
	}
}
