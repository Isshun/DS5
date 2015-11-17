package org.smallbox.faraway.core.game.module.world;

import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.engine.Color;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.engine.module.java.ModuleHelper;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameObserver;
import org.smallbox.faraway.core.game.model.Data;
import org.smallbox.faraway.core.game.model.WeatherInfo;
import org.smallbox.faraway.core.game.model.planet.PlanetInfo;
import org.smallbox.faraway.core.util.Utils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by Alex on 13/06/2015.
 */
public class WeatherModule extends GameModule implements GameObserver {
    private int                     _duration;
    private WeatherInfo             _weather;
    private String                  _dayTime = Data.config.time;

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

    public WeatherModule() {
        ModuleHelper.getWeatherModule(this);
    }

    @Override
    protected void onLoaded(Game game) {
        _lightTarget = 1;
        _lightProgress = 1;
        _weather = Data.getData().weathers.get("base.weather.regular");
        ModuleHelper.getWorldModule().setLight(1);
    }

    @Override
    protected boolean loadOnStart() {
        return Data.config.manager.weather;
    }

    @Override
    public void onHourChange(int hour) {
        PlanetInfo planetInfo = Game.getInstance().getPlanet().getInfo();
        if (planetInfo.dayTimes != null) {
            planetInfo.dayTimes.stream().filter(hourInfo -> hourInfo.hour == hour).forEach(this::setHour);
        }
    }

    private void setHour(PlanetInfo.DayTime hourInfo) {
        _lightChange = 1 / hourInfo.duration / Data.config.tickPerHour;
        _lightProgress = 0;
        _previousLight = _lightTarget;
        _lightTarget = hourInfo.light;
        _temperatureTarget = Game.getInstance().getInfo().region.temperature[1];
        switchSunColor(hourInfo.sun);

        Application.getInstance().notify(observer -> observer.onDayTimeChange(hourInfo));
    }

    public double getTemperature() { return _temperature; }
    public double getLight() { return 1; }

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

            Application.getInstance().notify(observer -> observer.onLightChange(_light, _lightColor));
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
                Application.getInstance().notify(observer -> observer.onTemperatureChange(_temperature));
            }
        }
    }

    private void loadRandomWeather() {
        List<String> allowedWeathers = Game.getInstance().getInfo().region.weather.stream().map(weather -> weather.name).collect(Collectors.toList());
        List<WeatherInfo> allWeathers = Data.getData().weathers.values().stream().collect(Collectors.toList());
        Collections.shuffle(allWeathers);
        Optional<WeatherInfo> optionalWeather = allWeathers.stream().filter(weather -> allowedWeathers.contains(weather.name)).findFirst();
        if (optionalWeather.isPresent()) {
            loadWeather(optionalWeather.get());
        } else {
            loadWeather(Data.getData().weathers.get("base.weather.regular"));
        }
    }

    private void loadWeather(WeatherInfo weather) {
        _weather = weather;

        printInfo("Start weather: " + _weather.name);
        Application.getInstance().notify(observer -> observer.onWeatherChange(weather));

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

    @Override
    public void onGameStart() {
        int hour = Game.getInstance().getHour();
        PlanetInfo planetInfo = Game.getInstance().getPlanet().getInfo();
        if (planetInfo.dayTimes != null) {
            for (PlanetInfo.DayTime hourInfo: planetInfo.dayTimes) {
                if (hour < hourInfo.hour) {
                    setHour(hourInfo);
                    return;
                }
            }
        }
    }
}
