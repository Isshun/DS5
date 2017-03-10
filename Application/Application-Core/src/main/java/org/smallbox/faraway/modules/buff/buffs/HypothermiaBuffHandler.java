package org.smallbox.faraway.modules.buff.buffs;

import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.modules.buff.BuffHandler;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.weather.WeatherModule;

/**
 * Created by Alex on 10/03/2017.
 */
public class HypothermiaBuffHandler extends BuffHandler {

    @BindModule
    private WeatherModule weatherModule;

    @Override
    protected int OnGetLevel(CharacterModel character) {

        if (weatherModule.getTemperature() < 20) {
            return 2;
        }

        if (weatherModule.getTemperature() < 50) {
            return 1;
        }

        return 0;
    }
}
