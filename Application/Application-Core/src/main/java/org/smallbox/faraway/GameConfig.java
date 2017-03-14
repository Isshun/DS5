package org.smallbox.faraway;

import org.smallbox.faraway.core.Application;

/**
 * Created by Alex on 14/03/2017.
 */
public abstract class GameConfig {

    protected static double byHour(double value) {
        return value / Application.config.game.tickPerHour;
    }

}
