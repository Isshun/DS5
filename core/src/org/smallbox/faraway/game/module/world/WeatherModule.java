package org.smallbox.faraway.game.module.world;

import org.smallbox.faraway.engine.Color;
import org.smallbox.faraway.engine.renderer.LightRenderer;
import org.smallbox.faraway.engine.renderer.ParticleRenderer;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.GameObserver;
import org.smallbox.faraway.game.module.GameModule;
import org.smallbox.faraway.game.model.GameData;
import org.smallbox.faraway.game.model.WeatherModel;
import org.smallbox.faraway.game.model.planet.PlanetInfo;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Alex on 13/06/2015.
 */
public class WeatherModule extends GameModule implements GameObserver {
    private LightRenderer           _lightRenderer;
    private ParticleRenderer        _particleRenderer;
    private WorldModule _worldModule;
    private int                     _duration;
    private WeatherModel            _weather;
    private String                  _dayTime = GameData.config.time;

    private Color                   _previousLightColor;
    private Color                   _nextLightColor;
    private double                  _lightChange;
    private double                  _lightProgress;
    private double                  _light;

    private PlanetInfo              _planetInfo;
    private TemperatureModule _temperatureModule;
    private LightModule _lightModule;

//    public WeatherModule(LightRenderer lightRenderer, ParticleRenderer particleRenderer, WorldModule worldManager) {
//        _lightRenderer = lightRenderer;
//        _particleRenderer = particleRenderer;
//        _worldModule = worldManager;
//        _dayTime = GameData.config.time;
//        _lightProgress = 1;
//    }

    @Override
    protected void onCreate() {
        _planetInfo = Game.getInstance().getPlanet().getInfo();
        _temperatureModule = (TemperatureModule)Game.getInstance().getModule(TemperatureModule.class);
        _lightModule = (LightModule)Game.getInstance().getModule(LightModule.class);
    }

    @Override
    protected boolean loadOnStart() {
        return GameData.config.manager.weather;
    }

    @Override
    public void onHourChange(int hour) {
        if (hour == _planetInfo.hours.dawn) {
            _lightChange = 1f / GameData.config.tickPerHour;
            _lightProgress = 0;
            _light = 0.5;
            switchSunColor("dawn");
        }
        if (hour == _planetInfo.hours.noon) {
            _lightChange = 0.5f / GameData.config.tickPerHour;
            _lightProgress = 0;
            _light = 1;
            _temperatureModule.setTemperature(Game.getInstance().getRegion().getInfo().temperature[1]);
            switchSunColor("noon");
        }
        if (hour == _planetInfo.hours.twilight) {
            _lightChange = 1f / GameData.config.tickPerHour;
            _lightProgress = 0;
            _light = 0.5;
            switchSunColor("twilight");
        }
        if (hour == _planetInfo.hours.midnight) {
            _lightChange = 0.5f / GameData.config.tickPerHour;
            _lightProgress = 0;
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
            loadWeather(new ArrayList<>(GameData.getData().weathers.values()).get((int) (Math.random() * GameData.getData().weathers.size())));
        }

        // Set light
        if (_lightProgress <= 1) {
            _lightProgress += _lightChange;
            Game.getWorldManager().setLight((int) (_light * _lightProgress));
            if (_lightRenderer != null) {
                _lightRenderer.setSunColor(new Color(
                        (int) ((_previousLightColor.r * (1 - _lightProgress)) + (_nextLightColor.r * _lightProgress)),
                        (int) ((_previousLightColor.g * (1 - _lightProgress)) + (_nextLightColor.g * _lightProgress)),
                        (int) ((_previousLightColor.b * (1 - _lightProgress)) + (_nextLightColor.b * _lightProgress))));
            }
        }
    }

    private void loadWeather(WeatherModel weather) {
        _weather = weather;

        printInfo("Start weather: " + _weather.name);

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
