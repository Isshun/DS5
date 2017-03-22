package org.smallbox.faraway.client.controller;

import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIImage;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.client.ui.engine.views.widgets.View;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.dependencyInjector.BindComponent;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.modelInfo.WeatherInfo;
import org.smallbox.faraway.modules.temperature.TemperatureModule;
import org.smallbox.faraway.modules.weather.WeatherModule;

/**
 * Created by Alex on 27/02/2017.
 */
public class SystemInfoController extends LuaController {

    @BindComponent
    private Game game;

    @BindLua
    private UILabel lbTime;

    @BindLua
    private View frameTime;

    @BindLua
    private UILabel lbDay;

    @BindLua
    private UILabel lbWeather;

    @BindLua
    private UIImage imgWeather;

    @BindLua
    private UILabel lbTemperature;

    @BindLua
    private UIImage icSpeed;

    @BindModule
    private WeatherModule weatherModule;

    @BindModule
    private TemperatureModule temperatureModule;

    @Override
    protected void onControllerUpdate() {
        lbTime.setText(game.getTime().getHour() + "H");
        frameTime.setHeight(game.getTime().getMinute() * 20 / 60);
        frameTime.setPositionY(28 - frameTime.getHeight());

        lbDay.setText("J-" + (game.getTime().getDay() + 1));

        icSpeed.setImage("[base]/graphics/ic_speed_" + game.getSpeed() + ".png");

        WeatherInfo weatherInfo = weatherModule.getWeather();
        if (weatherInfo != null) {
            lbWeather.setText(weatherInfo.label);
            lbWeather.setTextColor(weatherInfo.color2);
            imgWeather.setImage(weatherInfo.icon);
        }

        lbTemperature.setText(String.valueOf(weatherModule.getTemperature()));
    }

    @Override
    public void onGamePaused() {
        icSpeed.setImage("[base]/graphics/ic_speed_0.png");
    }

    @Override
    public void onGameResume() {
        icSpeed.setImage("[base]/graphics/ic_speed_" + Application.gameManager.getGame().getSpeed() + ".png");
    }
}
