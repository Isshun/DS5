//package org.smallbox.faraway.client.controller;
//
//import com.badlogic.gdx.Game;
//import com.badlogic.gdx.Input;
//import org.smallbox.faraway.client.GameShortcut;
//import org.smallbox.faraway.client.controller.annotation.BindLua;
//import org.smallbox.faraway.client.controller.annotation.BindLuaAction;
//import org.smallbox.faraway.client.controller.annotation.BindLuaController;
//import org.smallbox.faraway.client.ui.engine.views.widgets.UIImage;
//import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
//import org.smallbox.faraway.client.ui.engine.views.widgets.View;
//import org.smallbox.faraway.common.GameTime;
//import org.smallbox.faraway.common.dependencyInjector.BindComponent;
//import org.smallbox.faraway.common.dependencyInjector.GameObject;
//import org.smallbox.faraway.common.modelInfo.WeatherInfo;
//
///**
// * SystemInfoController
// *
// * Created by Alex on 27/02/2017.
// */
//@GameObject
//public class SystemInfoController extends LuaController {
//
//    @BindComponent
//    private Game game;
//
//    @BindComponent
//    private GameManager gameManager;
//
//    @BindLua
//    private View viewPause;
//
//    @BindLua
//    private View viewWeather;
//
//    @BindLua
//    private UILabel lbTime;
//
//    @BindLua
//    private UILabel lbDate;
//
//    @BindLua
//    private UILabel lbWeather;
//
//    @BindLua
//    private UIImage imgWeather;
//
//    @BindLua
//    private UILabel lbTemperature;
//
//    @BindLua
//    private UIImage icSpeed;
//
//    @BindLua
//    private UILabel lbSpeed;
//
//    @BindComponent
//    private WeatherModule weatherModule;
//
//    @BindLuaController
//    private MainPanelController mainPanelController;
//
//    @Override
//    protected void onControllerUpdate() {
//
//        // Display game time
//        GameTime time = game.getTime();
//        lbTime.setText(String.format("%02d:%02d", time.getHour(), time.getMinute()));
//        lbDate.setText(String.format("%02d/%02d/%d", time.getDay(), time.getMonth(), time.getYear()));
//
//        // Display weather, temperature and pressure
//        WeatherInfo weatherInfo = weatherModule.getWeather();
//        if (weatherInfo != null) {
//            viewWeather.setBackgroundColor(weatherInfo.color1);
//            lbWeather.setText(weatherInfo.label);
//            lbWeather.setTextColor(weatherInfo.color2);
//            imgWeather.setImage(weatherInfo.icon);
//            lbTemperature.setText(String.format("%.1f° / %.1fhp", weatherModule.getTemperature(), 12.7));
//            lbTemperature.setTextColor(weatherInfo.color2);
//        }
//
//        // Display game speed
//        icSpeed.setImage("[base]/graphics/ic_speed_" + game.getSpeed() + ".png");
//        lbSpeed.setText("x" + game.getSpeed());
//        icSpeed.setVisible(game.getSpeed() <= 3);
//        lbSpeed.setVisible(game.getSpeed() > 3);
//    }
//
//    @Override
//    public void onGamePaused() {
//        viewPause.setVisible(true);
//        icSpeed.setImage("[base]/graphics/ic_speed_0.png");
//    }
//
//    @Override
//    public void onGameResume() {
//        viewPause.setVisible(false);
//        icSpeed.setImage("[base]/graphics/ic_speed_" + game.getSpeed() + ".png");
//    }
//
//    @BindLuaAction
//    public void onActionMenu(View view) {
//        game.setRunning(false);
//    }
//
//    @BindLuaAction
//    public void onActionExit(View view) {
//        gameManager.closeGame();
//    }
//
//    @GameShortcut(key = Input.Keys.SPACE)
//    public void actionPause() {
//        game.toggleRunning();
//    }
//
//    @GameShortcut(key = Input.Keys.PLUS)
//    public void actionSpeedUp() {
//        game.setSpeed(game.getSpeed() + 1);
//    }
//
//    @GameShortcut(key = Input.Keys.MINUS)
//    public void actionSpeedDown() {
//        game.setSpeed(game.getSpeed() - 1);
//    }
//
//}
