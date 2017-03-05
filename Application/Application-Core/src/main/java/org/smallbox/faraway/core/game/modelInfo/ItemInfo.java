package org.smallbox.faraway.core.game.modelInfo;

import org.apache.commons.lang3.StringUtils;
import org.smallbox.faraway.core.module.world.model.ItemFilter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ItemInfo extends ObjectInfo {
    public boolean isLiquid;
    public ItemInfo surface;
    public File dataDirectory;

    public boolean hasCraftAction() {
        if (actions != null) {
            for (ItemInfoAction action : actions) {
                if (action.type == ItemInfoAction.ActionType.CRAFT || action.type == ItemInfoAction.ActionType.COOK) {
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

    public boolean hasGraphics() {
        return this.graphics != null && !this.graphics.isEmpty();
    }

    public static class ItemInfoStorage {
        public int[]                    components;
        public int[]                    crafts;
    }

    public static class ItemConsumeInfo {
        public int                      cost;
        public int                      count;
        public ItemInfoEffects          effects;
    }

    public static class ItemMaterialInfo {
        public String                   label;
        public String                   iconPath;
    }

    public static class ActionInputInfo {
        public ItemInfo                 item;
        public NetworkInfo              network;
        public int                      quantity;
    }

    public static class ItemInfoAction {
        public enum ActionType {USE, MINE, GATHER, BUILD, CRAFT, COOK, CUT}
        public int                      storage;
        public ActionType               type;
        public String                   label;
        public List<ItemInfoReceipt>    receipts;
        public ItemInfoEffects          effects;
        public List<ActionInputInfo>    inputs;
        public int                      cost;
        public String                   name;
        public float                    dropRate;
        public List<ItemProductInfo>    products;
        public boolean                  auto;
    }

    public static class ItemInfoPlant {
        public static class GrowingInfo {
            public String               name;
            public double               value;
            public double[]             temperature;
            public double[]             light;
            public double[]             oxygen;
            public double[]             moisture;
        }
        public static class PlantRangeInfo {
            public int                  min;
            public int                  best;
            public int                  max;
        }
        public List<GrowingInfo>        states;
        public double                   growing;
        public boolean                  cutOnGathering = true;
        public double                   nourish;
//        public double                   oxygen;
        public PlantRangeInfo           temperature;
        public PlantRangeInfo           oxygen;
        public PlantRangeInfo           moisture;
        public PlantRangeInfo           light;
    }

    public static class ItemInfoReceipt {
        public String                   name;
        public String                   label;
        public String                   icon;
        public List<ItemComponentInfo>  components;
        public List<ItemProductInfo>    products;
    }

    public enum FactoryOutputMode {GROUND, NETWORK};

    public static class ItemInfoFactory {
        public Set<ReceiptGroupInfo>    receiptGroups;
        public int[]                    inputSlots;
        public int[]                    outputSlots;
    }

    public static class ItemProductInfo {
        public ItemInfo                 item;
        public int[]                    quantity;
        public double                   rate = 1;
    }

    public static class ItemComponentInfo {
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
        public static class ItemBuildComponentInfo {
            public ItemInfo    component;
            public int         count;
        }

        public int                          cost;
        public List<ItemBuildComponentInfo> components = new ArrayList<>();
    }

    public static class NetworkItemInfo {
        public String                   name;
        public NetworkInfo              network;
        public int                      distance;
    }

    public static class ItemInfoEffects {
        public int                      food;
        public int                      drink;
        public int                      energy;
        public int                      happiness;
        public int                      health;
        public int                      relation;
        public int                      entertainment;
        public double                   oxygen;
        public int                      pressure;
        public int                      security;
        public int                      heat;
        public int                      heatPotency;
        public int                      cold;
        public int                      coldPotency;
        public int                      temperature;
        public int                      temperaturePotency;
    }

    public String                       desc;
    public String                       inherits;
    public String                       label;
    public String                       labelChild;
    public String                       category;
    public String                       type;
    public ItemInfoStorage              storage;
    public List<int[]>                  slots;
    public List<ReceiptGroupInfo.ReceiptInfo>        receipts;
    public int[]                        tiles;
    public boolean                      isWalkable = true;
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
    public boolean                      isDoor = false;
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
    public boolean                      isNetworkItem;
    public ItemBuildInfo                build;
    public int                          health = 1;
    public int                          power;
    public double                       permeability;
    public boolean                      isLive;
    public int                          stack;
    public List<ItemComponentInfo>      components;
    public List<ItemInfo>               childs = new ArrayList<>();
    public List<GraphicInfo>            graphics = new ArrayList<>();
    public ItemInfoFactory              factory;
    public ItemInfo                     parent;
    public String                       parentName;
    public boolean                      canSupportRoof;
    public List<NetworkItemInfo>        networks;
    public NetworkInfo                  network;
    public ItemConsumeInfo              consume;
    public boolean                      isRamp;
    public boolean                      isGround;
    public boolean                      isLinkDown;
    public boolean                      isWall = false;
    public int                          color;

    public ItemInfo() {
        width = 1;
        height = 1;
        isWalkable = true;
    }

    public boolean matchFilter(ItemInfoEffects effects, ItemFilter filter) {
        if (effects != null) {
            if (filter.effectEntertainment && effects.entertainment > 0) { return true; }
            if (filter.effectDrink && effects.drink > 0) { return true; }
            if (filter.effectEnergy && effects.energy > 0) { return true; }
            if (filter.effectFood && effects.food > 0) { return true; }
            if (filter.effectHappiness && effects.happiness > 0) { return true; }
            if (filter.effectHealth && effects.health > 0) { return true; }
            if (filter.effectRelation && effects.relation > 0) { return true; }
        }
        return false;
    }

    @Override
    public String toString() {
        if (StringUtils.isNotBlank(name)) {
            return name;
        }
        return label;
    }
}