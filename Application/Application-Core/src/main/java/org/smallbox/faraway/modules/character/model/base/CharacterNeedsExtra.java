package org.smallbox.faraway.modules.character.model.base;

import org.smallbox.faraway.core.game.modelInfo.CharacterInfo;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.world.model.ConsumableItem;
import org.smallbox.faraway.util.Constant;

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

    private Map<String, NeedEntry> _values = new HashMap<>();

    public CharacterNeedsExtra(CharacterInfo.NeedsInfo needsInfo) {
        // TODO: add each need to lua file
        _values.put(TAG_FOOD, new NeedEntry(TAG_FOOD, Constant.CHARACTER_INIT_FOOD, needsInfo.food));
        _values.put(TAG_DRINK, new NeedEntry(TAG_DRINK, Constant.CHARACTER_INIT_DRINK, needsInfo.drink));
        _values.put(TAG_ENERGY, new NeedEntry(TAG_ENERGY, Constant.CHARACTER_INIT_ENERGY, needsInfo.energy));
        _values.put(TAG_OXYGEN, new NeedEntry(TAG_OXYGEN, Constant.CHARACTER_INIT_OXYGEN, needsInfo.oxygen));
        _values.put(TAG_HAPPINESS, new NeedEntry(TAG_HAPPINESS, Constant.CHARACTER_INIT_HAPPINESS, needsInfo.happiness));
        _values.put(TAG_ENTERTAINMENT, new NeedEntry(TAG_ENTERTAINMENT, Constant.CHARACTER_INIT_ENTERTAINMENT, needsInfo.entertainment));
        _values.put(TAG_RELATION, new NeedEntry(TAG_RELATION, Constant.CHARACTER_INIT_RELATION, needsInfo.relation));
    }

    public NeedEntry get(String name) {
        return _values.get(name);
    }

    public Map<String, NeedEntry> getAll() {
        return _values;
    }

    public void addValue(String name, double value) {
        _values.get(name).addValue(value);
    }

    public void use(ConsumableItem consumable) {
        use(consumable.getInfo().consume.effects, 1);
    }

    public void use(ItemInfo.ItemInfoEffects effects, int cost) {
        if (effects != null) {
            addValue("energy", (double)effects.energy / cost);
            addValue("food", (double)effects.food / cost);
            addValue("drink", (double)effects.drink / cost);
            addValue("entertainment", (double)effects.entertainment / cost);
            addValue("relation", (double)effects.relation / cost);
            addValue("happiness", (double)effects.happiness / cost);
        }
    }

    public boolean hasEffect(NeedEntry need, ConsumableItem consumable) {
        return hasEffect(need, consumable.getInfo().consume.effects);
    }

    private boolean hasEffect(NeedEntry need, ItemInfo.ItemInfoEffects effects) {
        if (effects != null) {
            switch (need.name) {
                case "energy":
                    return effects.energy > 0;
                case "food":
                    return effects.food > 0;
                case "drink":
                    return effects.drink > 0;
                case "entertainment":
                    return effects.entertainment > 0;
                case "relation":
                    return effects.relation > 0;
                case "happiness":
                    return effects.happiness > 0;
            }
        }
        return false;
    }

}
