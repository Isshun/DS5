package org.smallbox.faraway.core.module.world.model;

/**
 * Created by Alex on 10/11/2015.
 */
public class ParcelEnvironment {
    public double               rubble;
    public double               dirt;
    public double               blood;
    public double               snow;

    public int getScore() {
        int score = 0;

        if (this.snow > 0) {
            score += 1;
        }
        if (this.blood > 0) {
            score += -5;
        }
        if (this.dirt > 0) {
            score += -5;
        }
        if (this.rubble > 0) {
            score += -5;
        }

        return score;
    }
}