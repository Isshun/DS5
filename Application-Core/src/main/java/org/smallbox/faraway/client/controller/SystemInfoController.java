package org.smallbox.faraway.client.controller;

import com.badlogic.gdx.Input;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.controller.annotation.BindLuaAction;
import org.smallbox.faraway.client.render.Viewport;
import org.smallbox.faraway.client.ui.engine.views.View;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIFrame;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIImage;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.core.GameShortcut;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameManager;
import org.smallbox.faraway.core.game.GameTime;
import org.smallbox.faraway.core.game.modelInfo.WeatherInfo;
import org.smallbox.faraway.modules.weather.WeatherModule;

@GameObject
public class SystemInfoController extends LuaController {
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
            lbWeather.setTextColor(weatherInfo.color2);
            imgWeather.setImage(weatherInfo.icon);
            lbTemperature.setText(String.format("%.1f° / %.1fhp", weatherModule.getTemperature(), 12.7));
            lbTemperature.setTextColor(weatherInfo.color2);
        }

        // Display game speed
        icSpeed.setImage("[base]/graphics/ic_speed_" + game.getSpeed() + ".png");
        lbSpeed.setText("x" + game.getSpeed());
        icSpeed.setVisible(game.getSpeed() <= 3);
        lbSpeed.setVisible(game.getSpeed() > 3);
        lbFloor.setText(String.valueOf(viewport.getFloor()));
    }

    @Override
    public void onGamePaused() {
        icSpeed.setImage("[base]/graphics/ic_speed_0.png");
    }

    @Override
    public void onGameResume() {
        icSpeed.setImage("[base]/graphics/ic_speed_" + game.getSpeed() + ".png");
    }

    @BindLuaAction
    public void onActionMenu(View view) {
        game.setRunning(false);
    }

    @GameShortcut(key = Input.Keys.SPACE)
    public void actionPause() {
        game.toggleRunning();
    }

    @GameShortcut(key = Input.Keys.PLUS)
    public void actionSpeedUp() {
        game.setSpeed(game.getSpeed() + 1);
    }

    @GameShortcut(key = Input.Keys.MINUS)
    public void actionSpeedDown() {
        game.setSpeed(game.getSpeed() - 1);
    }

}
