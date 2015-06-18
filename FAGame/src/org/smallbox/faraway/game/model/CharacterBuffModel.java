package org.smallbox.faraway.game.model;

/**
 * Created by Alex on 14/06/2015.
 */
public class CharacterBuffModel {
    public final BuffModel                  buff;
    public BuffModel.BuffLevelModel         level;
    public int                              maxLevelIndex;
    public double                           progress;
    public int                              levelIndex = -1;

    public CharacterBuffModel(BuffModel buff) {
        this.buff = buff;
        for (BuffModel.BuffLevelModel level: buff.levels) {
            this.maxLevelIndex = level.index;
        }
    }

    public boolean isActive() {
        return this.level != null;
    }

    public int getMood() {
        return this.level != null && this.level.effects != null ? this.level.effects.mood : 0;
    }
}
