package org.smallbox.faraway.modules.character.model.base;

import org.smallbox.faraway.core.game.modelInfo.CharacterInfo;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CharacterNeedsExtra extends CharacterExtra {
    public final static String TAG_ENERGY = "energy";
    public final static String TAG_OXYGEN = "oxygen";
    public final static String TAG_FOOD = "food";
    public final static String TAG_DRINK = "drink";
    public final static String TAG_RELATION = "relation";
    public final static String TAG_ENTERTAINMENT = "entertainment";
    public final static String TAG_HAPPINESS = "happiness";

    private final Map<String, NeedEntry> _values = new HashMap<>();

    public CharacterNeedsExtra(CharacterModel character, CharacterInfo.NeedsInfo needsInfo) {
        super(character);

        // TODO: add each need to lua file
        _values.put(TAG_FOOD, new NeedEntry(TAG_FOOD, 1, needsInfo.food));
        _values.put(TAG_DRINK, new NeedEntry(TAG_DRINK, 1, needsInfo.drink));
        _values.put(TAG_ENERGY, new NeedEntry(TAG_ENERGY, 1, needsInfo.energy));
        _values.put(TAG_OXYGEN, new NeedEntry(TAG_OXYGEN, 1, needsInfo.oxygen));
        _values.put(TAG_HAPPINESS, new NeedEntry(TAG_HAPPINESS, 1, needsInfo.happiness));
        _values.put(TAG_ENTERTAINMENT, new NeedEntry(TAG_ENTERTAINMENT, 1, needsInfo.entertainment));
        _values.put(TAG_RELATION, new NeedEntry(TAG_RELATION, 1, needsInfo.relation));
    }

    public NeedEntry get(String name) {
        return _values.get(name);
    }

    public double getValue(String name) {
        return Optional.ofNullable(_values.get(name)).map(NeedEntry::value).orElse(0.0);
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
