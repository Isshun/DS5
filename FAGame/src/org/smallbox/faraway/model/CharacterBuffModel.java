package org.smallbox.faraway.model;

/**
 * Created by Alex on 14/06/2015.
 */
public class CharacterBuffModel {
    public final BuffModel  buff;
    public int              duration;
    public int              level = -1;

    public CharacterBuffModel(BuffModel buff) {
        this.buff = buff;
    }

    public boolean isActive() {
        return level > -1 && (level > 0 || duration >= buff.levels.get(level).delay);
    }

    public BuffModel.BuffLevelModel getActiveLevel() {
        if (level < 0) {
            return null;
        }
        if (duration >= buff.levels.get(level).delay) {
            return buff.levels.get(level);
        }
        if (level > 0) {
            return buff.levels.get(level - 1);
        }
        return null;
    }
}
