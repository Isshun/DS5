package org.smallbox.faraway.game.model.item;

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

	public boolean hasTemperatureEffect() {
		return effects != null && (effects.coldPotency != 0 || effects.heatPotency != 0);
	}

	public static class ItemInfoStorage {
		public int[]					components;
		public int[]					crafts;
	}

	public static class ItemInfoAction {
		public int 						storage;
		public String 					type;
		public String 					label;
		public List<ItemInfoReceipt>	receipts;
		public ItemInfoEffects			effects;
		public int 						mature;
		public int                  	cost;
		public String 					name;
		public float                	dropRate;
		public List<ItemProductInfo>	products;
	}

	public static class ItemInfoReceipt {
		public String 					name;
		public String 					label;
		public List<ItemComponentInfo>	components;
		public List<ItemProductInfo> 	products;
	}

	public static class ItemProductInfo {
		public String 					item;
		public ItemInfo 				itemInfo;
		public int 						quantity;
		public float 					dropRate;
	}

	public static class ItemComponentInfo {
		public String 					item;
		public ItemInfo 				itemInfo;
		public int 						quantity;
	}
	public static class EquipmentEffectBuff {
		public int                      sight;
		public int                      grow;
		public int                      repair;
		public int                      build;
		public int                      craft;
		public int                      cook;
		public int                      speed;
		public int                      tailoring;
		public double                   oxygen;
	}

	public static class EquipmentEffectAbsorb {
		public int                      cold;
		public int                      heat;
		public int                      damage;
	}

	public static class EquipmentEffectResist {
		public int                      cold;
		public int                      heat;
		public int                      damage;
		public int                      oxygen;
	}

	public static class ItemEquipmentInfo {
		public String 					location;
		public List<EquipmentEffect>	effects;
	}

	public static class EquipmentEffectCondition {
		public int                      minSight;
		public int                      maxSight;
	}

	public static class EquipmentEffect {
		public EquipmentEffectCondition condition;
		public EquipmentEffectAbsorb    absorb;
		public EquipmentEffectResist    resist;
		public EquipmentEffectBuff      buff;
	}

	public static class ItemInfoEffects {
		public int						food;
		public int 						drink;
		public int 						energy;
		public int 						happiness;
		public int 						health;
		public int 						relation;
		public double 					oxygen;
		public int 						socialize;
		public int 						security;
		public int 						heat;
		public int 						heatPotency;
		public int 						cold;
		public int 						coldPotency;
		public int 						temperature;
		public int 						temperaturePotency;
	}

	public String 						name;
	public String 						desc;
	public String 						inherits;
	public String 						label;
	public String 						category;
	public String 						type;
	public ItemInfoStorage				storage;
	public List<int[]>					slots;
	public List<ItemInfoReceipt>		receipts;
	public int[]						tiles;
	public boolean 						isWalkable;
	public int 							frames;
	public int 							framesInterval;
	public int 							width;
	public int 							height;
	public int 							light;
	public int 							cost;
	public List<ItemInfoAction> 		actions;
	public ItemEquipmentInfo 			equipment;
	public ItemInfoEffects 				effects;
	public boolean 						isEquipment;
	public boolean 						isStructure;
	public boolean 						isResource;
	public boolean 						isConsumable;
	public boolean 						isUserItem;
	public boolean 						isFood;
	public boolean 						isToy;
	public boolean 						isBed;
	public int 							spriteId;
	public String						fileName;
	public String 						packageName;
	public boolean 						isFactory;
	public boolean 						isDrink;
	public boolean 						isSleeping;
	public int 							maxHealth;
	public int 							power;
	public double 						sealing;

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
			if (filter.effectHapiness && effects.happiness > 0) { return true; }
			if (filter.effectHealth && effects.health > 0) { return true; }
			if (filter.effectRelation && effects.relation > 0) { return true; }
		}
		return false;
	}

}