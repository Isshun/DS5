package org.smallbox.faraway.module.weather;

import org.smallbox.faraway.core.engine.module.ModuleObserver;
import org.smallbox.faraway.core.game.modelInfo.WeatherInfo;

/**
 * Created by Alex on 15/07/2016.
 */
public interface WeatherModuleObserver extends ModuleObserver {
    void onWeatherChange(WeatherInfo weather);
    void onTemperatureChange(double temperature);
}
