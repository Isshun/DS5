package org.smallbox.faraway.client.controller;

import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIImage;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.core.dependencyInjector.BindComponent;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameTime;
import org.smallbox.faraway.core.game.modelInfo.WeatherInfo;
import org.smallbox.faraway.modules.weather.WeatherModule;

/**
 * SystemInfoController
 *
 * Created by Alex on 27/02/2017.
 */
public class SystemInfoController extends LuaController {

    @BindComponent
    private Game game;

    @BindLua
    private UILabel lbTime;

    @BindLua
    private UILabel lbDate;

    @BindLua
    private UILabel lbWeather;

    @BindLua
    private UIImage imgWeather;

    @BindLua
    private UILabel lbTemperature;

    @BindLua
    private UIImage icSpeed;

    @BindLua
    private UILabel lbSpeed;

    @BindModule
    private WeatherModule weatherModule;

    @Override
    protected void onControllerUpdate() {

        // Display game time
        GameTime time = game.getTime();
        lbTime.setText(String.format("%02d:%02d", time.getHour(), time.getMinute()));
        lbDate.setText(String.format("%02d/%02d/%d", time.getDay(), time.getMonth(), time.getYear()));

        // Display weather, temperature and pressure
        WeatherInfo weatherInfo = weatherModule.getWeather();
        if (weatherInfo != null) {
            lbWeather.setText(weatherInfo.label);
            lbWeather.setTextColor(weatherInfo.color2);
            imgWeather.setImage(weatherInfo.icon);
            lbTemperature.setText(String.format("%.1f° / %.1fhp", weatherModule.getTemperature(), 12.7));
            lbTemperature.setTextColor(weatherInfo.color2);
        }

        // Display game speed
        if (game.getSpeed() <= 3) {
            icSpeed.setImage("[base]/graphics/ic_speed_" + game.getSpeed() + ".png");
            icSpeed.setVisible(true);
            lbSpeed.setVisible(false);
        } else {
            icSpeed.setVisible(false);
            lbSpeed.setVisible(true);
            lbSpeed.setText("x" + game.getSpeed());
        }

    }

    @Override
    public void onGamePaused() {
        icSpeed.setImage("[base]/graphics/ic_speed_0.png");
    }

    @Override
    public void onGameResume() {
        icSpeed.setImage("[base]/graphics/ic_speed_" + game.getSpeed() + ".png");
    }
}
