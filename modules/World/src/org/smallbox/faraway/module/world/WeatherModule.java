package org.smallbox.faraway.module.world;

import org.smallbox.faraway.core.engine.Color;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameObserver;
import org.smallbox.faraway.core.game.model.GameData;
import org.smallbox.faraway.core.game.model.WeatherModel;
import org.smallbox.faraway.core.game.model.planet.PlanetInfo;
import org.smallbox.faraway.core.game.model.planet.RegionInfo;
import org.smallbox.faraway.core.module.GameModule;
import org.smallbox.faraway.core.module.java.ModuleHelper;
import org.smallbox.faraway.core.util.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by Alex on 13/06/2015.
 */
public class WeatherModule extends GameModule implements GameObserver {
    private int                     _duration;
    private WeatherModel            _weather;
    private String                  _dayTime = GameData.config.time;

    private long                    _lightColor;
    private Color                   _previousLightColor;
    private Color                   _nextLightColor;

    private double                  _lightChange;
    private double                  _lightProgress;
    private double                  _lightTarget;
    private double                  _previousLight;
    private double                  _light;

    private double                  _temperature;
    private double                  _temperatureTarget;
    private double                  _temperatureOffset;

    @Override
    protected void onLoaded() {
        _lightTarget = 1;
        _lightProgress = 1;
        _weather = GameData.getData().weathers.get("base.weather.regular");
        ModuleHelper.getWorldModule().setLight(1);
    }

    @Override
    protected boolean loadOnStart() {
        return GameData.config.manager.weather;
    }

    @Override
    public void onHourChange(int hour) {
        PlanetInfo planetInfo = Game.getInstance().getPlanet().getInfo();

        if (hour == planetInfo.hours.dawn) {
            _lightChange = 1f / GameData.config.tickPerHour;
            _lightProgress = 0;
            _previousLight = _lightTarget;
            _lightTarget = 0.5;
            switchSunColor("dawn");
        }
        if (hour == planetInfo.hours.noon) {
            _lightChange = 0.5f / GameData.config.tickPerHour;
            _lightProgress = 0;
            _previousLight = _lightTarget;
            _lightTarget = 1;
            _temperatureTarget = Game.getInstance().getRegion().getInfo().temperature[1];
            switchSunColor("noon");
        }
        if (hour == planetInfo.hours.twilight) {
            _lightChange = 1f / GameData.config.tickPerHour;
            _lightProgress = 0;
            _previousLight = _lightTarget;
            _lightTarget = 0.5;
            switchSunColor("twilight");
        }
        if (hour == planetInfo.hours.midnight) {
            _lightChange = 0.5f / GameData.config.tickPerHour;
            _lightProgress = 0;
            _previousLight = _lightTarget;
            _lightTarget = 0.2;
            _temperatureTarget = Game.getInstance().getRegion().getInfo().temperature[0];
//            _temperatureChange = (Game.getInstance().getRegion().getInfo().temperature[0] - Game.getInstance().getRegion().getInfo().temperature[1]) / 120.0;
            switchSunColor("midnight");
        }
    }

    @Override
    protected void onUpdate(int tick) {
        if (_duration-- <= 0) {
            _duration = 2500;
            loadRandomWeather();
        }

        // Set light
        if (_lightProgress <= 1) {

            // Get current light strength
            _lightProgress += _lightChange;
            _light = Math.max(0, Math.min(1, _previousLight * (1 - _lightProgress) + (_lightTarget * _lightProgress)));

            // Get current light color
            if (_previousLightColor != null && _nextLightColor != null) {
                _lightColor = (int) ((_previousLightColor.r * (1 - _lightProgress)) + (_nextLightColor.r * _lightProgress));
                _lightColor = (_lightColor << 8) + (int) ((_previousLightColor.g * (1 - _lightProgress)) + (_nextLightColor.g * _lightProgress));
                _lightColor = (_lightColor << 8) + (int) ((_previousLightColor.b * (1 - _lightProgress)) + (_nextLightColor.b * _lightProgress));
            }

            Game.getInstance().notify(observer -> observer.onLightChange(_light, _lightColor));
        }

        // Set temperature
        double change = ((_temperatureTarget + _temperatureOffset) - _temperature) / 100;
        if (change != 0) {
            double temperature = _temperature;
            if (change > -0.001 && change < 0.001) {
                temperature = _temperatureTarget + _temperatureOffset;
            } else if (change > -0.01 && change < 0.01) {
                temperature += change < 0 ? -0.01 : 0.01;
            } else {
                temperature += change;
            }

            boolean significantChange = (int)(_temperature * 10) != (int)(temperature * 10);
            _temperature = temperature;
            if (significantChange) {
                Game.getInstance().notify(observer -> observer.onTemperatureChange(_temperature));
            }
        }
    }

    private void loadRandomWeather() {
        List<String> allowedWeathers = Game.getInstance().getRegion().getInfo().weather.stream().map(weather -> weather.name).collect(Collectors.toList());
        List<WeatherModel> allWeathers = GameData.getData().weathers.values().stream().collect(Collectors.toList());
        Collections.shuffle(allWeathers);
        Optional<WeatherModel> optionalWeather = allWeathers.stream().filter(weather -> allowedWeathers.contains(weather.name)).findFirst();
        if (optionalWeather.isPresent()) {
            loadWeather(optionalWeather.get());
        } else {
            loadWeather(GameData.getData().weathers.get("base.weather.regular"));
        }
    }

    private void loadWeather(WeatherModel weather) {
        _weather = weather;

        printInfo("Start weather: " + _weather.name);
        Game.getInstance().notify(observer -> observer.onWeatherChange(weather));

        // Sun color
        switchSunColor(_dayTime);

        // Set temperature offset
        _temperatureOffset = _weather.temperatureChange != null ? weather.temperatureChange[0] + Utils.getRandom(_weather.temperatureChange) : 0;
    }

    /**
     * Sun color
     */
    private void switchSunColor(String dayTime) {
        _dayTime = dayTime;

        switch (dayTime) {
            case "dawn":
                _previousLightColor = new Color(_weather.sun.midnight);
                _nextLightColor = new Color(_weather.sun.dawn);
                break;
            case "twilight":
                _previousLightColor = new Color(_weather.sun.noon);
                _nextLightColor = new Color(_weather.sun.twilight);
                break;
            case "midnight":
                _previousLightColor = new Color(_weather.sun.twilight);
                _nextLightColor = new Color(_weather.sun.midnight);
                break;
            default:
                _previousLightColor = new Color(_weather.sun.dawn);
                _nextLightColor = new Color(_weather.sun.noon);
                break;
        }
    }

    public WeatherModel getWeather() {
        return _weather;
    }

    public void next() {
        loadWeather(new ArrayList<>(GameData.getData().weathers.values()).get((int)(Math.random() * GameData.getData().weathers.size())));
    }
}
