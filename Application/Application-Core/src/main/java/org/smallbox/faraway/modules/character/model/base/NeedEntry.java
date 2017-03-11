package org.smallbox.faraway.modules.character.model.base;

import org.smallbox.faraway.core.game.modelInfo.CharacterInfo;
import org.smallbox.faraway.util.Utils;

/**
 * Created by Alex on 11/03/2017.
 */
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
}
