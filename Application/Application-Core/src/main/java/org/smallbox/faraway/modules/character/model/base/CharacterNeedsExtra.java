package org.smallbox.faraway.modules.character.model.base;

import org.smallbox.faraway.common.modelInfo.CharacterInfo;
import org.smallbox.faraway.common.modelInfo.ItemInfo;
import org.smallbox.faraway.common.util.Constant;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CharacterNeedsExtra extends CharacterExtra {
    public final static String TAG_ENERGY = "energy";
    public final static String TAG_OXYGEN = "oxygen";
    public final static String TAG_FOOD = "food";
    public final static String TAG_DRINK = "drink";
    public final static String TAG_RELATION = "relation";
    public final static String TAG_ENTERTAINMENT = "entertainment";
    public final static String TAG_HAPPINESS = "happiness";

    private Map<String, NeedEntry> _values = new HashMap<>();

    public CharacterNeedsExtra(CharacterModel character, CharacterInfo.NeedsInfo needsInfo) {
        super(character);

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

    public Collection<NeedEntry> getAll() {
        return _values.values();
    }

    public void addValue(String name, double value) {
        _values.get(name).addValue(value);
    }

    public void use(ItemInfo.ItemActionInfo action, double tickPerHour) {
        if (action != null && action.effects != null) {
            use(action.effects, action.duration, tickPerHour);
        }
    }

    public void use(ItemInfo.ItemInfoEffects effects, double duration, double tickPerHour) {
        if (effects != null && duration != 0) {
            addValue("energy", effects.energy / (tickPerHour * duration));
            addValue("food", effects.food / (tickPerHour * duration));
            addValue("drink", effects.drink / (tickPerHour * duration));
            addValue("entertainment", effects.entertainment / (tickPerHour * duration));
            addValue("relation", effects.relation / (tickPerHour * duration));
            addValue("happiness", effects.happiness / (tickPerHour * duration));
        }
    }

}
