package org.smallbox.faraway.modules.character.model.base;

import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.game.modelInfo.CharacterInfo;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.util.Constant;

import java.util.Collection;
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

    public Collection<NeedEntry> getAll() {
        return _values.values();
    }

    public void addValue(String name, double value) {
        _values.get(name).addValue(value);
    }

    public void use(ItemInfo.ItemActionInfo action) {
        if (action != null && action.effects != null) {
            use(action.effects, action.duration);
        }
    }

    public void use(ItemInfo.ItemInfoEffects effects, double duration) {
        if (effects != null && duration != 0) {
            addValue("energy", effects.energy / (duration / Application.gameManager.getGame().getTickPerHour()));
            addValue("food", effects.food / (duration / Application.gameManager.getGame().getTickPerHour()));
            addValue("drink", effects.drink / (duration / Application.gameManager.getGame().getTickPerHour()));
            addValue("entertainment", effects.entertainment / (duration / Application.gameManager.getGame().getTickPerHour()));
            addValue("relation", effects.relation / (duration / Application.gameManager.getGame().getTickPerHour()));
            addValue("happiness", effects.happiness / (duration / Application.gameManager.getGame().getTickPerHour()));
        }
    }

}
