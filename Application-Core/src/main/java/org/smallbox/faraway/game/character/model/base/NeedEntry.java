package org.smallbox.faraway.game.character.model.base;

import org.smallbox.faraway.game.character.CharacterInfo;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.util.Utils;

public class NeedEntry {
    public final String name;
    public final double optimal;
    public final double warning;
    public final double critical;
    private double value;

    public NeedEntry(String name, double value, CharacterInfo.NeedInfo needInfo) {
        this.value = value;
        this.optimal = needInfo.optimal;
        this.warning = needInfo.warning;
        this.critical = needInfo.critical;
        this.name = name;
    }

    public double value() {
        return this.value;
    }

    public void addValue(double value) {
        this.value = Utils.bound(0, 1, this.value + value);
    }

    public boolean hasEffect(ItemInfo.ItemActionInfo action) {
        if (action != null && action.effects != null) {
            switch (name) {
                case "energy":
                    return action.effects.energy > 0;
                case "food":
                    return action.effects.food > 0;
                case "drink":
                    return action.effects.drink > 0;
                case "entertainment":
                    return action.effects.entertainment > 0;
                case "relation":
                    return action.effects.relation > 0;
                case "happiness":
                    return action.effects.happiness > 0;
            }
        }
        return false;
    }
}
