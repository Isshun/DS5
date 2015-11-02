package org.smallbox.faraway.module.world;

import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.engine.Color;
import org.smallbox.faraway.core.engine.renderer.LightRenderer;
import org.smallbox.faraway.core.engine.renderer.ParticleRenderer;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameObserver;
import org.smallbox.faraway.core.game.model.GameData;
import org.smallbox.faraway.core.game.model.WeatherModel;
import org.smallbox.faraway.core.game.model.planet.PlanetInfo;
import org.smallbox.faraway.core.module.GameModule;
import org.smallbox.faraway.core.module.java.ModuleHelper;
import org.smallbox.faraway.core.module.java.ModuleManager;
import org.smallbox.faraway.core.game.module.world.WorldModule;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Alex on 13/06/2015.
 */
public class WeatherModule extends GameModule implements GameObserver {
    private LightRenderer           _lightRenderer;
    private ParticleRenderer        _particleRenderer;
    private WorldModule             _worldModule;
    private int                     _duration;
    private WeatherModel            _weather;
    private String                  _dayTime = GameData.config.time;

    private Color                   _previousLightColor;
    private Color                   _nextLightColor;
    private double                  _lightChange;
    private double                  _lightProgress;
    private double                  _light;
    private double                  _previousLight;

    private TemperatureModule       _temperatureModule;
    private LightModule             _lightModule;

    @Override
    protected void onLoaded() {
        _temperatureModule = (TemperatureModule) ModuleManager.getInstance().getModule(TemperatureModule.class);
        _lightModule = (LightModule)ModuleManager.getInstance().getModule(LightModule.class);
        _light = 1;
        _lightProgress = 1;
        ModuleHelper.getWorldModule().setLight(1);
    }

    @Override
    protected boolean loadOnStart() {
        return GameData.config.manager.weather;
    }

    @Override
    public void onHourChange(int hour) {
        PlanetInfo planetInfo = Game.getInstance().getPlanet().getInfo();

        loadRandomWeather();

        if (hour == planetInfo.hours.dawn) {
            _lightChange = 1f / GameData.config.tickPerHour;
            _lightProgress = 0;
            _previousLight = _light;
            _light = 0.5;
            switchSunColor("dawn");
        }
        if (hour == planetInfo.hours.noon) {
            _lightChange = 0.5f / GameData.config.tickPerHour;
            _lightProgress = 0;
            _previousLight = _light;
            _light = 1;
            _temperatureModule.setTemperature(Game.getInstance().getRegion().getInfo().temperature[1]);
            switchSunColor("noon");
        }
        if (hour == planetInfo.hours.twilight) {
            _lightChange = 1f / GameData.config.tickPerHour;
            _lightProgress = 0;
            _previousLight = _light;
            _light = 0.5;
            switchSunColor("twilight");
        }
        if (hour == planetInfo.hours.midnight) {
            _lightChange = 0.5f / GameData.config.tickPerHour;
            _lightProgress = 0;
            _previousLight = _light;
            _light = 0.2;
            _temperatureModule.setTemperature(Game.getInstance().getRegion().getInfo().temperature[0]);
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
            _lightProgress += _lightChange;
            ModuleHelper.getWorldModule().setLight(Math.max(0, Math.min(1, _previousLight * (1 - _lightProgress) + (_light * _lightProgress))));
            if (_lightRenderer != null) {
                _lightRenderer.setSunColor(new Color(
                        (int) ((_previousLightColor.r * (1 - _lightProgress)) + (_nextLightColor.r * _lightProgress)),
                        (int) ((_previousLightColor.g * (1 - _lightProgress)) + (_nextLightColor.g * _lightProgress)),
                        (int) ((_previousLightColor.b * (1 - _lightProgress)) + (_nextLightColor.b * _lightProgress))));
            }
        }
    }

    private void loadRandomWeather() {
        loadWeather(new ArrayList<>(GameData.getData().weathers.values()).get((int) (Math.random() * GameData.getData().weathers.size())));
    }

    private void loadWeather(WeatherModel weather) {
        _weather = weather;

        printInfo("Start weather: " + _weather.name);
        Game.getInstance().notify(observer -> observer.onWeatherChange(weather));

        // Sun color
        switchSunColor(_dayTime);

        // Particle buffEffect
        if (_particleRenderer != null) {
            _particleRenderer.setParticle(_weather.particle);
        }

        // World temperature
        if (_weather.temperatureChange != null) {
            if (_weather.temperatureChange[1] > _weather.temperatureChange[0]) {
                Random random = new Random();
                _temperatureModule.setTemperatureOffset(_weather.temperatureChange[0] + random.nextInt(Math.abs(_weather.temperatureChange[1] - _weather.temperatureChange[0])));
            } else {
                _temperatureModule.setTemperatureOffset(_weather.temperatureChange[0]);
            }
        } else {
            _temperatureModule.setTemperatureOffset(0);
        }
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
