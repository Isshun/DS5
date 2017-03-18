package org.smallbox.faraway.modules.weather;

import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.engine.Color;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameObserver;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.model.planet.PlanetInfo;
import org.smallbox.faraway.core.game.model.planet.RegionInfo;
import org.smallbox.faraway.core.game.modelInfo.WeatherInfo;
import org.smallbox.faraway.core.game.modelInfo.WeatherInfo.WeatherSunModel;
import org.smallbox.faraway.util.CollectionUtils;
import org.smallbox.faraway.util.Utils;

import java.util.Collections;
import java.util.List;

/**
 * Created by Alex on 13/06/2015.
 */
public class WeatherModule extends GameModule<WeatherModuleObserver> implements GameObserver {
    private int                                 _duration;
    private int                                 _floors;
    private WeatherInfo                         _weather;
    private String                              _dayTime = "noon";

    private long                                _lightColor;
    private Color                               _previousLightColor;
    private Color                               _nextLightColor;

    private double                              _lightChange;
    private double                              _lightProgress;
    private double                              _lightTarget;
    private double                              _previousLight;
    private double                              _light;

    private double                              _temperatureOffset;
    private List<RegionInfo.RegionTemperature>  _temperatures;
    private double[]                            _temperatureByFloor;
    private double[]                            _temperatureTargetByFloor;
    private double                              _temperature;

    @Override
    public void onGameStart(Game game) {
        _floors = game.getInfo().worldFloors;
        _temperatures = game.getInfo().region.temperatures;
        _temperatureByFloor = new double[_floors];
        _temperatureTargetByFloor = new double[_floors];
        _lightTarget = 1;
        _lightProgress = 1;
        _weather = game.getInfo().region.weather.get(0).info;

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

    @Override
    public void onHourChange(int hour) {
        // Set temperature for all floors
        for (int floor = 0; floor < _floors; floor++) {
            for (RegionInfo.RegionTemperature regionTemperature: _temperatures) {
                if (floor - _floors + 1 >= regionTemperature.fromFloor && floor - _floors + 1 <= regionTemperature.toFloor) {
                    _temperature = regionTemperature.temperature[0];
                    _temperatureByFloor[floor] = regionTemperature.temperature[0];
                    _temperatureTargetByFloor[floor] = regionTemperature.temperature[0];
                }
            }
        }

        PlanetInfo planetInfo = Application.gameManager.getGame().getPlanet().getInfo();
        if (planetInfo.dayTimes != null) {
            planetInfo.dayTimes.stream().filter(dayTime -> dayTime.hour == hour).forEach(this::setHour);
        }

        notifyObservers(observer -> observer.onTemperatureChange(Math.random() * 40));
    }

    private void setHour(PlanetInfo.DayTime hourInfo) {
//        _lightChange = 1 / hourInfo.duration / Application.config.game.tickPerHour;
//        _lightProgress = 0;
//        _previousLight = _lightTarget;
//        _lightTarget = hourInfo.light;

        if (_weather != null && _weather.sun != null) {
            switchSunColor(_weather.sun, _dayTime);
        }

        Application.notify(observer -> observer.onDayTimeChange(hourInfo));
    }

    public WeatherInfo getWeather() { return _weather; }
    public double getTemperature() { return _temperature; }
    public double getTemperatureFloor(int floor) { return _temperatureByFloor != null ? _temperatureByFloor[floor] : 0; }
    public double getLight() { return 1; }
    public double getOxygen() { return 0.5; }

    @Override
    protected void onModuleUpdate(Game game) {
        if (_duration-- <= 0) {
            _duration = 2500;
            loadWeather(getRandomWeather(Application.gameManager.getGame().getInfo().region.weather));
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

            notifyObservers(observer -> observer.onLightChange(_light, _lightColor));
        }

        // Set temperature
        for (int floor = 0; floor < _floors; floor++) {
            double change = ((_temperatureTargetByFloor[floor] + _temperatureOffset) - _temperatureByFloor[floor]) / 100;
            if (change > -0.001 && change < 0.001) _temperatureByFloor[floor] += _temperatureOffset;
            else if (change > -0.01 && change < 0.01) _temperatureByFloor[floor] += change < 0 ? -0.01 : 0.01;
            else _temperatureByFloor[floor] += change;
        }

        notifyObservers(observer -> observer.onTemperatureChange(_temperatureByFloor[WorldHelper.getGroundFloor()]));
    }

    private WeatherInfo getRandomWeather(List<RegionInfo.RegionWeather> weatherList) {

        if (CollectionUtils.isNotEmpty(weatherList)) {
            Collections.shuffle(weatherList);
            return weatherList.get(0).info;
        }
        return Application.data.weathers.get("base.weather.regular");
    }

    private void loadWeather(WeatherInfo weather) {
        _weather = weather;

        printInfo("Start weather: " + _weather.name);
        notifyObservers(observer -> observer.onWeatherChange(weather));

        // Sun color
        if (weather.sun != null) {
            switchSunColor(weather.sun, _dayTime);
        }

        // Set temperature offset
        _temperatureOffset = _weather.temperatureChange != null ? weather.temperatureChange[0] + Utils.getRandom(_weather.temperatureChange) : 0;
    }

    /**
     * Sun color
     */
    private void switchSunColor(WeatherSunModel sun, String dayTime) {
        _dayTime = dayTime;

        switch (dayTime) {
            case "dawn":
                _previousLightColor = new Color(sun.midnight);
                _nextLightColor = new Color(sun.dawn);
                break;
            case "twilight":
                _previousLightColor = new Color(sun.noon);
                _nextLightColor = new Color(sun.twilight);
                break;
            case "midnight":
                _previousLightColor = new Color(sun.twilight);
                _nextLightColor = new Color(sun.midnight);
                break;
            default:
                _previousLightColor = new Color(sun.dawn);
                _nextLightColor = new Color(sun.noon);
                break;
        }
    }
}