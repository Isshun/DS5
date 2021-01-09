package org.smallbox.faraway.client.debug.interpreter.moduleInterpreter.impl;

import org.apache.commons.lang3.RandomUtils;
import org.smallbox.faraway.client.debug.interpreter.moduleInterpreter.ConsoleCommand;
import org.smallbox.faraway.client.debug.interpreter.moduleInterpreter.ConsoleInterpreterBase;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.modules.weather.WeatherModule;

@GameObject
public class WeatherModuleConsoleInterpreter extends ConsoleInterpreterBase {

    @Inject
    private WeatherModule weatherModule;

    @Inject
    private Data data;

    @ConsoleCommand("random")
    public void getList() {
        data.weathers.values().stream().skip(RandomUtils.nextInt(0, data.weathers.size() - 1)).findFirst().ifPresent(weatherInfo -> weatherModule.loadWeather(weatherInfo));
    }

}
