package org.smallbox.faraway.client.controller;

import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.controller.annotation.BindLuaAction;
import org.smallbox.faraway.client.renderer.Viewport;
import org.smallbox.faraway.client.shortcut.GameShortcut;
import org.smallbox.faraway.client.ui.widgets.UIFrame;
import org.smallbox.faraway.client.ui.widgets.UIImage;
import org.smallbox.faraway.client.ui.widgets.UILabel;
import org.smallbox.faraway.client.ui.widgets.View;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameManager;
import org.smallbox.faraway.core.game.GameTime;
import org.smallbox.faraway.game.weather.WeatherInfo;
import org.smallbox.faraway.game.weather.WeatherModule;
import org.smallbox.faraway.game.weather.WorldTemperatureModule;

@GameObject
public class SystemInfoController extends LuaController {
    @Inject private WorldTemperatureModule worldTemperatureModule;
    @Inject private Game game;
    @Inject private GameTime gameTime;
    @Inject private GameManager gameManager;
    @Inject private WeatherModule weatherModule;
    @Inject private MainPanelController mainPanelController;
    @Inject private Viewport viewport;

    @BindLua private View viewWeather;
    @BindLua private UILabel lbTime;
    @BindLua private UILabel lbDate;
    @BindLua private UILabel lbWeather;
    @BindLua private UIImage imgWeather;
    @BindLua private UILabel lbTemperature;
    @BindLua private UIImage icSpeed;
    @BindLua private UIImage imgDaytime;
    @BindLua private UILabel lbSpeed;
    @BindLua private UILabel lbFloor;
    @BindLua private UIFrame mapContainer;

    public UIFrame getMapContainer() {
        return mapContainer;
    }

    @Override
    protected void onControllerUpdate() {

        // Display game time
        lbTime.setText(String.format("%02d:%02d", gameTime.getHour(), gameTime.getMinute()));
        lbDate.setText(String.format("%02d/%02d/%d", gameTime.getDay(), gameTime.getMonth(), gameTime.getYear()));

        // Display weather, temperature and pressure
        WeatherInfo weatherInfo = weatherModule.getWeather();
        if (weatherInfo != null) {
            viewWeather.getStyle().setBackgroundColor(weatherInfo.color1);
            lbWeather.setText(weatherInfo.label);
//            lbWeather.setTextColor(weatherInfo.color2);
            imgWeather.setImage(weatherInfo.icon);
            lbTemperature.setText(String.format("%d° / %.1fhp", Math.round(worldTemperatureModule.getTemperature()), 12.7));
//            lbTemperature.setTextColor(weatherInfo.color2);
        }

        // Display game speed
        icSpeed.setImage("data/graphics/ic_speed_" + (game.isRunning() ? 0 : game.getSpeed()) + ".png");
        imgDaytime.setImage("data/graphics/icons/daytimes/" + weatherModule.getDaytime().name + ".png");
        lbSpeed.setText("x" + game.getSpeed());
        icSpeed.setVisible(game.getSpeed() <= 3);
        lbSpeed.setVisible(game.getSpeed() > 3);
        lbFloor.setText(String.valueOf(viewport.getFloor()));
    }

    @BindLuaAction
    public void onActionMenu(View view) {
        game.setRunning(false);
    }

    @GameShortcut("game/pause")
    public void actionPause() {
        game.toggleRunning();
    }

    @GameShortcut("game/speed_up")
    public void actionSpeedUp() {
        game.setSpeed(game.getSpeed() + 1);
    }

    @GameShortcut("game/speed_down")
    public void actionSpeedDown() {
        game.setSpeed(game.getSpeed() - 1);
    }

}
