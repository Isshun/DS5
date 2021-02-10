package org.smallbox.faraway.client.debug.dashboard.content;

import org.smallbox.faraway.client.debug.dashboard.DashboardLayerBase;
import org.smallbox.faraway.client.renderer.BaseRenderer;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.DataManager;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.game.weather.WeatherModule;

@GameObject
public class WeatherDashboardLayer extends DashboardLayerBase {
    @Inject private DataManager dataManager;
    @Inject private WeatherModule weatherModule;
    @Inject private Game game;

    @Override
    protected void onDraw(BaseRenderer renderer, int frame) {
        game.getPlanetInfo().dayTimes.values().forEach(dayTime -> drawDebug(renderer, "DAYTIME", dayTime.name, () -> weatherModule.loadDayTime(dayTime)));
        dataManager.weathers.values().forEach(weatherInfo -> drawDebug(renderer, "WEATHER", weatherInfo.label, () -> weatherModule.loadWeather(weatherInfo)));
    }

}
