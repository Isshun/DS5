package org.smallbox.faraway.modules.weather;

import org.smallbox.faraway.core.engine.module.ModuleObserver;
import org.smallbox.faraway.core.game.modelInfo.WeatherInfo;

/**
 * Created by Alex on 15/07/2016.
 */
public interface WeatherModuleObserver extends ModuleObserver {
    default void onWeatherChange(WeatherInfo weather) {}
    default void onTemperatureChange(double temperature) {}
    default void onLightChange(double light, long color) {}
}
