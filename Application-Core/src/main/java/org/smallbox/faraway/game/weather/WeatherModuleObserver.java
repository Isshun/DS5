package org.smallbox.faraway.game.weather;

import org.smallbox.faraway.core.engine.module.ModuleObserver;

public interface WeatherModuleObserver extends ModuleObserver {
    default void onWeatherChange(WeatherInfo weather) {}
    default void onTemperatureChange(double temperature) {}
    default void onLightChange(double light, long color) {}
}
