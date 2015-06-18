package org.smallbox.faraway.game.manager;

import org.smallbox.faraway.engine.Color;
import org.smallbox.faraway.game.GameObserver;
import org.smallbox.faraway.engine.renderer.LightRenderer;
import org.smallbox.faraway.engine.renderer.ParticleRenderer;
import org.smallbox.faraway.util.Log;
import org.smallbox.faraway.game.model.GameData;
import org.smallbox.faraway.game.model.WeatherModel;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Alex on 13/06/2015.
 */
public class WeatherManager extends BaseManager implements GameObserver {
    private final LightRenderer _lightRenderer;
    private final ParticleRenderer _particleRenderer;
    private final WorldManager      _worldManager;
    private int                     _duration;
    private WeatherModel            _weather;
    private String                  _dayTime;

    private double                  _sunTransitionProgress;
    private Color _previousSunColor;
    private Color                   _nextSunColor;
    private float                   _progressValue;

    public WeatherManager(LightRenderer lightRenderer, ParticleRenderer particleRenderer, WorldManager worldManager) {
        _lightRenderer = lightRenderer;
        _particleRenderer = particleRenderer;
        _worldManager = worldManager;

        _dayTime = "midnight";
        _sunTransitionProgress = 1;
    }

    @Override
    public void onHourChange(int hour) {
        if (hour == 5) {
            _progressValue = 1f / GameData.config.tickPerHour;
            _sunTransitionProgress = 0;
            switchSunColor("dawn");
        }
        if (hour == 6) {
            _progressValue = 0.5f / GameData.config.tickPerHour;
            _sunTransitionProgress = 0;
            switchSunColor("noon");
        }
        if (hour == 19) {
            _progressValue = 1f / GameData.config.tickPerHour;
            _sunTransitionProgress = 0;
            switchSunColor("twilight");
        }
        if (hour == 20) {
            _progressValue = 0.5f / GameData.config.tickPerHour;
            _sunTransitionProgress = 0;
            switchSunColor("midnight");
        }
    }

    @Override
    protected void onUpdate(int tick) {
        if (_duration-- <= 0) {
            _duration = 2500;
            loadWeather(new ArrayList<>(GameData.getData().weathers.values()).get((int)(Math.random() * GameData.getData().weathers.size())));
        }

        if (_sunTransitionProgress < 1) {
            _sunTransitionProgress += _progressValue;
            _lightRenderer.setSunColor(new Color(
                    (int)((_previousSunColor.r * (1-_sunTransitionProgress)) + (_nextSunColor.r * _sunTransitionProgress)),
                    (int)((_previousSunColor.g * (1-_sunTransitionProgress)) + (_nextSunColor.g * _sunTransitionProgress)),
                    (int)((_previousSunColor.b * (1-_sunTransitionProgress)) + (_nextSunColor.b * _sunTransitionProgress))));
        }
    }

    private void loadWeather(WeatherModel weather) {
        _weather = weather;

        Log.info("Start weather: " + _weather.name);

        // Sun color
        switchSunColor(_dayTime);

        // Particle effect
        _particleRenderer.setParticle(_weather.particle);

        // World temperature
        if (_weather.temperatureChange != null) {
            if (_weather.temperatureChange[1] > _weather.temperatureChange[0]) {
                Random random = new Random();
                _worldManager.setTemperatureOffset(_weather.temperatureChange[0] + random.nextInt(Math.abs(_weather.temperatureChange[1] - _weather.temperatureChange[0])));
            } else {
                _worldManager.setTemperatureOffset(_weather.temperatureChange[0]);
            }
        } else {
            _worldManager.setTemperatureOffset(0);
        }
    }

    /**
     * Sun color
     */
    private void switchSunColor(String dayTime) {
        _dayTime = dayTime;

        switch (dayTime) {
            case "dawn":
                _previousSunColor = new Color(_weather.sun.midnight);
                _nextSunColor = new Color(_weather.sun.dawn);
                break;
            case "twilight":
                _previousSunColor = new Color(_weather.sun.noon);
                _nextSunColor = new Color(_weather.sun.twilight);
                break;
            case "midnight":
                _previousSunColor = new Color(_weather.sun.twilight);
                _nextSunColor = new Color(_weather.sun.midnight);
                break;
            default:
                _previousSunColor = new Color(_weather.sun.dawn);
                _nextSunColor = new Color(_weather.sun.noon);
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
