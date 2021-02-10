package org.smallbox.faraway.game.weather;

import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.client.asset.music.BackgroundMusicManager;
import org.smallbox.faraway.core.config.ApplicationConfig;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.OnGameNewDay;
import org.smallbox.faraway.core.game.DataManager;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameObserver;
import org.smallbox.faraway.core.game.GameTime;
import org.smallbox.faraway.core.module.SuperGameModule2;
import org.smallbox.faraway.game.planet.PlanetInfo;
import org.smallbox.faraway.game.planet.RegionInfo;
import org.smallbox.faraway.util.Random;
import org.smallbox.faraway.util.log.Log;
import org.smallbox.faraway.util.transition.ColorTransition;
import org.smallbox.faraway.util.transition.DoubleTransition;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static java.time.temporal.ChronoUnit.HOURS;

@GameObject
public class WeatherModule extends SuperGameModule2<WeatherModuleObserver> implements GameObserver {
    @Inject private DataManager dataManager;
    @Inject private ApplicationConfig applicationConfig;
    @Inject private GameTime gameTime;
    @Inject private Game game;
    @Inject private BackgroundMusicManager backgroundMusicManager;

    private int _floors;
    private WeatherInfo _weather;
    private double temperatureVariation;
    private double dailyTemperatureVariation;
    private double hourlyTemperatureVariation;
    private double monthlyTemperature;
    private double _temperature;
    private LocalDateTime lastChange;
    private ColorTransition ambientLightTransition;
    private DoubleTransition temperatureTransition;

    private Color ambientLight = new Color();
    private PlanetInfo.DayTime currentDayTime;
    private RegionInfo.RegionSeason currentSeason;

    @Override
    public void onGameStart(Game game) {
        _floors = game.getInfo().worldFloors;
        _weather = game.getInfo().region.weather.get(0).info;
        currentDayTime = game.getInfo().planet.dayTimes.get(0);

        Random.ofNullable(dataManager.weathers).ifPresent(this::loadWeather);

        // TODO
//        ModuleHelper.getWorldModule().setLight(1);

//        int hour = Application.gameManager.getGame().getHour();
//        PlanetInfo planetInfo = Application.gameManager.getGame().getPlanet().getInfo();
//        if (planetInfo.dayTimes != null) {
//            for (PlanetInfo.DayTime hourInfo: planetInfo.dayTimes) {
//                if (hour < hourInfo.hour) {
//                    setHour(hourInfo);
//                    return;
//                }
//            }
//        }
    }

    @OnGameNewDay
    public void refreshSeason() {
        currentSeason = game.getRegionInfo().seasons.stream()
                .filter(season -> gameTime.getMonth() > season.from || (gameTime.getMonth() == season.from && gameTime.getDay() >= season.dayOfMonth))
                .filter(season -> gameTime.getMonth() < season.to || (gameTime.getMonth() == season.to && gameTime.getDay() < season.dayOfMonth))
                .findFirst().orElse(null);
    }

    @Override
    public void onGameLongUpdate(Game game) {

        // Change weather
        if (gameTime.now().isAfter(lastChange.plus(24, HOURS))) {
            loadWeather(Random.ofNullable(game.getInfo().region.weather).map(regionWeather -> regionWeather.info).orElse(dataManager.weathers.get("base.weather.regular")));
        }

        // Check light
        PlanetInfo.DayTime dayTime = game.getPlanetInfo().dayTimes.get(gameTime.getHour());
        if (dayTime != null && dayTime != currentDayTime) {
            loadDayTime(dayTime);
        }

//        // Set temperature
//        updateFloorTemperature(game);
//        for (int floor = 0; floor < _floors; floor++) {
//            double change = ((_temperatureTargetByFloor[floor] + _temperatureOffset) - _temperatureByFloor[floor]) / 100;
//            if (change > -0.001 && change < 0.001) _temperatureByFloor[floor] += _temperatureOffset;
//            else if (change > -0.01 && change < 0.01) _temperatureByFloor[floor] += change < 0 ? -0.01 : 0.01;
//            else _temperatureByFloor[floor] += change;
//        }
//
//        notifyObservers(observer -> observer.onTemperatureChange(_temperatureByFloor[WorldHelper.getGroundFloor()]));
    }

    @Override
    public void onGameUpdate(Game game) {

        // Set light
        if (ambientLightTransition != null) {
            ambientLight = new Color(ambientLightTransition.getValue(gameTime.now()));
        }

    }

    public void loadDayTime(PlanetInfo.DayTime dayTime) {
        Log.info("Set daytime to " + dayTime);
        ambientLightTransition = new ColorTransition(currentDayTime.color, dayTime.color);
        ambientLightTransition.setInterval(gameTime.now(), gameTime.plus(1, TimeUnit.HOURS));
        currentDayTime = dayTime;
    }

    public void loadWeather(WeatherInfo weather) {
        lastChange = gameTime.now();

        if (_weather != null && _weather.music != null && weather.music == null) {
            backgroundMusicManager.playRandom();
        }

        _weather = weather;

        Log.info(WeatherModule.class, "Start weather: " + _weather.name);
        notifyObservers(observer -> observer.onWeatherChange(weather));

        // Set temperature offset
        temperatureVariation = _weather.temperatureChange != null ? weather.temperatureChange[0] + Random.interval(_weather.temperatureChange) : 0;

        if (weather.music != null) {
            backgroundMusicManager.play(weather.music);
        }
    }

    public Color getAmbientLight() {
        return ambientLight;
    }

    public WeatherInfo getWeather() {
        return _weather;
    }

    public double getLight() {
        return 1;
    }

    public double getOxygen() {
        return 0.5;
    }

    public PlanetInfo.DayTime getDaytime() {
        return currentDayTime;
    }

    public double getTemperatureVariation() {
        return temperatureVariation;
    }
}