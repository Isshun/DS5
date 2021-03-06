package org.smallbox.faraway.client.debug.interpreter.moduleInterpreter.impl;

import org.smallbox.faraway.client.debug.interpreter.moduleInterpreter.ConsoleCommand;
import org.smallbox.faraway.client.debug.interpreter.moduleInterpreter.ConsoleInterpreterBase;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.DataManager;
import org.smallbox.faraway.game.weather.WeatherModule;
import org.smallbox.faraway.util.Random;

@GameObject
public class WeatherModuleConsoleInterpreter extends ConsoleInterpreterBase {
    @Inject private WeatherModule weatherModule;
    @Inject private DataManager dataManager;

    private int index;

    @ConsoleCommand("random")
    public void actionRandom() {
        Random.ofNullable(dataManager.weathers).ifPresent(weatherInfo -> weatherModule.loadWeather(weatherInfo));
    }

    @ConsoleCommand("next")
    public void actionNext() {
        dataManager.weathers.values().stream().skip(index++ % dataManager.weathers.size() - 1).findFirst().ifPresent(weatherInfo -> weatherModule.loadWeather(weatherInfo));
    }

}
