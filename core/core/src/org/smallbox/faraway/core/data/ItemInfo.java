package org.smallbox.faraway.core.data;

import org.smallbox.faraway.core.GraphicInfo;
import org.smallbox.faraway.core.game.module.world.model.ItemFilter;
import org.smallbox.faraway.core.game.module.world.model.ReceiptGroupInfo;

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

    public boolean hasTemperatureEffect() {
        return effects != null && (effects.coldPotency != 0 || effects.heatPotency != 0);
    }

    public boolean instanceOf(ItemInfo inputInfo) {
        return this == inputInfo || this.parent == inputInfo;
    }

    public static class ItemInfoStorage {
        public int[]                    components;
        public int[]                    crafts;
    }

    public static class ItemInfoAction {
        public int                      storage;
        public String                   type;
        public String                   label;
        public List<ItemInfoReceipt>    receipts;
        public ItemInfoEffects          effects;
        public int                      cost;
        public String                   name;
        public float                    dropRate;
        public List<ItemProductInfo>    products;
        public List<ItemProductInfo>    finalProducts;
    }

    public static class ItemInfoPlant {
        public static class GrowingInfo {
            public String               name;
            public double               value;
            public int[]                temperature;
            public int[]                light;
        }
        public List<GrowingInfo>        states;
        public double                   growing;
        public double                   minMaturity;
        public boolean                  cutOnGathering = true;
        public double                   nourish;
    }

    public static class ItemInfoReceipt {
        public String                   name;
        public String                   label;
        public List<ItemComponentInfo>  components;
        public List<ItemProductInfo>    products;
    }

    public static class ItemInfoFactory {
        public List<ReceiptGroupInfo>   receipts;
        public List<String>             receiptNames;
        public int[]                    inputSlots;
        public int[]                    outputSlots;
    }

    public static class ItemProductInfo {
        public String                   itemName;
        public ItemInfo                 item;
        public int[]                    quantity;
        public double                   rate = 1;
    }

    public static class ItemComponentInfo {
        public String                   itemName;
        public ItemInfo                 item;
        public int                      quantity;
    }
//    public static class EquipmentEffectBuff {
//        public int                      sight;
//        public int                      grow;
//        public int                      repair;
//        public int                      build;
//        public int                      craft;
//        public int                      cook;
//        public int                      speed;
//        public int                      tailoring;
//        public double                   oxygen;
//    }

    public static class EquipmentEffectValues {
        public int                      cold;
        public int                      heat;
        public int                      damage;
        public int                      oxygen;
        public int                      sight;
        public int                      grow;
        public int                      repair;
        public int                      build;
        public int                      craft;
        public int                      cook;
        public int                      speed;
        public int                      tailoring;
    }

    public static class ItemEquipmentInfo {
        public String                   location;
        public List<EquipmentEffect>    effects;
    }

    public static class EquipmentEffectCondition {
        public int                      minSight;
        public int                      maxSight;
    }

    public static class EquipmentEffect {
        public EquipmentEffectCondition condition;
        public EquipmentEffectValues    resist;
        public EquipmentEffectValues    debuff;
        public EquipmentEffectValues    buff;
        public EquipmentEffectValues    absorb;
    }

    public static class ItemBuildInfo {
        public int                      cost;
    }

    public static class ItemInfoEffects {
        public int                      food;
        public int                      drink;
        public int                      energy;
        public int                      happiness;
        public int                      health;
        public int                      relation;
        public int                      joy;
        public double                   oxygen;
        public int                      socialize;
        public int                      security;
        public int                      heat;
        public int                      heatPotency;
        public int                      cold;
        public int                      coldPotency;
        public int                      temperature;
        public int                      temperaturePotency;
    }

    public String                       name;
    public String                       desc;
    public String                       inherits;
    public String                       label;
    public String                       labelChild;
    public String                       category;
    public String                       type;
    public ItemInfoStorage              storage;
    public List<int[]>                  slots;
    public List<ItemInfoReceipt>        receipts;
    public int[]                        tiles;
    public boolean                      isWalkable;
    public int                          frames;
    public int                          framesInterval;
    public int                          width;
    public int                          height;
    public int                          light;
    public int                          lightDistance;
    public int                          cost = 10;
    public List<ItemInfoAction>         actions;
    public ItemEquipmentInfo            equipment;
    public ItemInfoEffects              effects;
    public ItemInfoPlant                plant;
    public boolean                      isDoor;
    public boolean                      isEquipment;
    public boolean                      isStructure;
    public boolean                      isResource;
    public boolean                      isConsumable;
    public boolean                      isUserItem;
    public boolean                      isFood;
    public boolean                      isToy;
    public boolean                      isBed;
    public boolean                      isRock;
    public boolean                      isPlant;
    public String                       sciLabel;
    public boolean                      isFactory;
    public boolean                      isDrink;
    public boolean                      isCloseRoom;
    public boolean                      isFloor;
    public ItemBuildInfo                build;
    public int                          maxHealth;
    public int                          power;
    public double                       sealing;
    public boolean                      isLive;
    public int                          stack;
    public ItemInfo                     parentInfo;
    public List<ItemComponentInfo>      components;
    public List<ItemInfo>               childs = new ArrayList<>();
    public List<GraphicInfo>            graphics = new ArrayList<>();
    public ItemInfoFactory              factory;
    public ItemInfo                     parent;
    public String                       parentName;
    public boolean                      canSupportRoof;

    public ItemInfo() {
        width = 1;
        height = 1;
        isWalkable = true;
    }

    public boolean matchFilter(ItemInfoEffects effects, ItemFilter filter) {
        if (effects != null) {
            if (filter.effectEntertainment && effects.joy > 0) { return true; }
            if (filter.effectDrink && effects.drink > 0) { return true; }
            if (filter.effectEnergy && effects.energy > 0) { return true; }
            if (filter.effectFood && effects.food > 0) { return true; }
            if (filter.effectHappiness && effects.happiness > 0) { return true; }
            if (filter.effectHealth && effects.health > 0) { return true; }
            if (filter.effectRelation && effects.relation > 0) { return true; }
        }
        return false;
    }
}