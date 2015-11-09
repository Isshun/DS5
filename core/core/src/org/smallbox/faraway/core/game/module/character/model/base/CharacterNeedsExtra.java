package org.smallbox.faraway.core.game.module.character.model.base;

import org.smallbox.faraway.core.data.ItemInfo;
import org.smallbox.faraway.core.data.ItemInfo.ItemInfoAction;
import org.smallbox.faraway.core.game.model.CharacterTypeInfo;
import org.smallbox.faraway.core.game.model.Data;
import org.smallbox.faraway.core.game.module.world.model.MapObjectModel;
import org.smallbox.faraway.core.module.java.ModuleHelper;
import org.smallbox.faraway.core.util.Constant;

import java.util.HashMap;
import java.util.Map;

public class CharacterNeedsExtra {
    public final static String TAG_ENERGY = "energy";
    public final static String TAG_OXYGEN = "oxygen";
    public final static String TAG_FOOD = "food";
    public final static String TAG_DRINK = "drink";
    public final static String TAG_RELATION = "relation";
    public final static String TAG_ENTERTAINMENT = "entertainment";
    public final static String TAG_HAPPINESS = "happiness";

    private final CharacterModel     _character;
    private final Data _data;

    // Actions
    public boolean isSleeping;
    private int    _eating;

    // Stats
    private Map<String, Double> _values = new HashMap<>();
    public double     socialize;
    public double   happinessChange;
    public double     health;
    public double     sickness;
    public double     injuries;
    public double     heat;
    public double   heatDifferenceReal;
    public int         environment;
    public int         light;
    public int         pain;

    private MapObjectModel     _sleepItem;
    private CharacterStatsExtra _stats;
    private boolean isFainting;

    public CharacterNeedsExtra(CharacterModel character, CharacterStatsExtra stats) {
        _data = Data.getData();
        _character = character;
        _stats = stats;
        _sleepItem = null;
        _values.put(TAG_FOOD, Constant.CHARACTER_INIT_FOOD + (Math.random() * 100) % 40 - 20);
        _values.put(TAG_OXYGEN, Constant.CHARACTER_INIT_OXYGEN + (Math.random() * 100) % 40 - 20);
        _values.put(TAG_HAPPINESS, Constant.CHARACTER_INIT_HAPPINESS + (Math.random() * 100) % 40 - 20);
        _values.put(TAG_ENERGY, Constant.CHARACTER_INIT_ENERGY + (Math.random() * 100) % 40 - 20);
        _values.put(TAG_ENTERTAINMENT, 0.0);
        _values.put(TAG_RELATION, 0.0);
        _values.put("security", 0.0);

        _values.put(TAG_ENERGY, 100.0);
        _values.put(TAG_DRINK, 100.0);
        _values.put(TAG_FOOD, 100.0);
        _values.put(TAG_OXYGEN, 100.0);
        _values.put(TAG_RELATION, 100.0);
        _values.put(TAG_ENTERTAINMENT, 100.0);

        health = (float) (Constant.CHARACTER_INIT_HEALTH + (Math.random() * 100) % 20 - 10);
        heat = character.getType().needs.heat.optimal;
        injuries = 0;
        sickness = 0;
    }

    public double   get(String name) { return _values.containsKey(name) ? _values.get(name) : -1; }

    public boolean    isSleeping() { return isSleeping; }

    public void    update() {
        CharacterTypeInfo.Needs needs = _character.getType().needs;

        addValue(TAG_ENERGY, isSleeping ? needs.energy.change.sleep : needs.energy.change.wake);
        addValue(TAG_FOOD, isSleeping ? needs.food.change.sleep : needs.food.change.wake);
        addValue(TAG_DRINK, isSleeping ? needs.water.change.sleep : needs.water.change.wake);
        addValue(TAG_ENTERTAINMENT, isSleeping ? needs.joy.change.sleep : needs.joy.change.wake);
        addValue(TAG_RELATION, ModuleHelper.getCharacterModule().havePeopleOnProximity(_character) ? 1 : -0.25);
        addValue(TAG_HAPPINESS, happinessChange / 100);

        // Oxygen
        double oxygen = _values.get("oxygen");
        if (_character.getParcel() != null) {
            int oxygenLevel = (int)(_character.getParcel().getOxygen() * 100);
            if (oxygen < oxygenLevel || oxygen > oxygenLevel + 1) {
                // Increase oxygen
                if (oxygen < oxygenLevel) {
                    oxygen += 1;
                }
                // Decrease oxygen, use resist
                else {
                    oxygen -= 1 * (1 - _stats.resist.oxygen / 100f);
                }
            }
        }
        _values.put(TAG_OXYGEN, oxygen);

        // Set needs bounds
        _values.entrySet().forEach(entry -> entry.setValue(Math.max(0, Math.min(100, entry.getValue()))));
    }

    public void addValue(String name, double value) {
        _values.put(name, Math.max(0, Math.min(100, (_values.containsKey(name) ? _values.get(name) : 0) + value)));
    }

    public void setValue(String name, double value) {
        _values.put(name, Math.max(0, Math.min(100, value)));
    }

    public void updateNeeds(CharacterTypeInfo.Needs needs) {
//
//        // Body heat
//        // TODO
////        double heatDifference = _character.getParcel().getTemperature() - (this.heat - _character.getType().thermolysis);
//        double heatDifference = 0;
//        double heatDifferenceReal = 0;
////        System.out.println("heatDifference: " + heatDifference);
//
//        if (heatDifference < 0) {
//            heatDifferenceReal = Math.min(0, heatDifference + _stats.buff.heat);
//        } else if (heatDifference > 0) {
//            heatDifferenceReal = Math.max(0, heatDifference - _stats.buff.cold);
//        }
//
//        this.heatDifferenceReal = heatDifferenceReal;
//
////        System.out.println("heatDifferenceReal: " + heatDifferenceReal);
//
//        if (heatDifferenceReal < 0) {
//            this.heat += heatDifferenceReal * (1 - _stats.resist.cold / 100f) / 100f;
//        } else if (heatDifferenceReal > 0) {
//            this.heat += heatDifferenceReal * (1 - _stats.resist.heat / 100f) / 100f;
//        } else {
//            if (this.heat > _character.getType().needs.heat.optimal + 0.25) {
//                this.heat += (heatDifference - _stats.buff.cold) / 1000f;
//            } else if (this.heat < _character.getType().needs.heat.optimal - 0.25) {
//                this.heat += (heatDifference + _stats.buff.heat) / 1000f;
//            } else {
//                this.heat = _character.getType().needs.heat.optimal;
//            }
//        }

//        System.out.println("bodyHeat: " + this.heat);
    }

    public void use(MapObjectModel item, ItemInfo.ItemInfoEffects effects, int cost) {
        if (item.isSleepingItem()) {
            _sleepItem = item;
            isSleeping = true;
        } else {
            _sleepItem = null;
        }

        if (effects != null) {
            addValue("energy", (double)effects.energy / cost);
            addValue("food", (double)effects.food / cost);
            addValue("drink", (double)effects.drink / cost);
            addValue("entertainment", (double)effects.entertainment / cost);
            addValue("relation", (double)effects.relation / cost);
            addValue("happiness", (double)effects.happiness / cost);
            health = Math.min(health + (double)effects.health / cost, 100);
        }
    }

    public void setSleeping(boolean isSleeping) {
        this.isSleeping = isSleeping;
    }
}
