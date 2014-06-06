package alone.in.deepspace.model.item;


public class ItemFilter {
	public boolean 	effectFood;
	public boolean 	effectDrink;
	public boolean 	effectEnergy;
	public boolean 	effectHapiness;
	public boolean 	effectRelation;
	public boolean 	effectHealth;
	public boolean 	isFactory;
	public boolean 	isImmediate;
	public ItemInfo	itemMatched;
	public ItemInfo itemNeeded;
	public boolean 	hasFreeSlot;
	
	public ItemFilter(boolean isFactory, boolean isImmediate) {
		this.isFactory = isFactory;
		this.isImmediate = isImmediate;
	}
}
