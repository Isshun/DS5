package org.smallbox.faraway.core.game.model.characterBuff.handler;

import org.smallbox.faraway.common.dependencyInjector.BindComponent;
import org.smallbox.faraway.common.dependencyInjector.GameObject;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.weather.WeatherModule;

/**
 * Created by Alex on 10/03/2017.
 */
@GameObject
public class HypothermiaBuffHandler extends BuffHandler {

    @BindComponent
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
