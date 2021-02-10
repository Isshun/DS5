package org.smallbox.faraway.game.characterBuff.handler;

import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.game.character.model.base.CharacterModel;
import org.smallbox.faraway.game.weather.WeatherModule;
import org.smallbox.faraway.game.weather.WorldTemperatureModule;

@GameObject
public class HypothermiaBuffHandler extends BuffHandler {
    @Inject private WorldTemperatureModule worldTemperatureModule;
    @Inject private WeatherModule weatherModule;

    @Override
    protected int OnGetLevel(CharacterModel character) {

        if (worldTemperatureModule.getTemperature() < 20) {
            return 2;
        }

        if (worldTemperatureModule.getTemperature() < 50) {
            return 1;
        }

        return 0;
    }
}
