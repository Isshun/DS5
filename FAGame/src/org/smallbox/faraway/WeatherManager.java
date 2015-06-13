package org.smallbox.faraway;

import org.smallbox.faraway.engine.util.Log;
import org.smallbox.faraway.manager.WorldManager;
import org.smallbox.faraway.model.GameData;
import org.smallbox.faraway.model.PlanetModel;
import org.smallbox.faraway.model.WeatherModel;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Alex on 13/06/2015.
 */
public class WeatherManager implements WorldObserver {
    private final LightRenderer     _lightRenderer;
    private final ParticleRenderer  _particleRenderer;
    private final WorldManager      _worldManager;
    private int                     _duration;
    private WeatherModel            _weather;
    private String                  _dayTime;

    public WeatherManager(LightRenderer lightRenderer, ParticleRenderer particleRenderer, WorldManager worldManager) {
        _lightRenderer = lightRenderer;
        _particleRenderer = particleRenderer;
        _worldManager = worldManager;
        _dayTime = "noon";
    }

    public void onHourChange(PlanetModel planet, int hour) {
        if (hour == 6) {
            switchSunColor("dawn");
        }
        if (hour == 7) {
            switchSunColor("noon");
        }
        if (hour == 20) {
            switchSunColor("twilight");
        }
        if (hour == 21) {
            switchSunColor("midnight");
        }
    }

    public void update(int update) {
        if (_duration-- <= 0) {
            _duration = 100;
            loadWeather(new ArrayList<>(GameData.getData().weathers.values()).get((int)(Math.random() * GameData.getData().weathers.size())));
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
            case "dawn": _lightRenderer.setSunColor(new Color(_weather.sun.dawn)); break;
            case "twilight": _lightRenderer.setSunColor(new Color(_weather.sun.twilight)); break;
            case "midnight": _lightRenderer.setSunColor(new Color(_weather.sun.midnight)); break;
            default: _lightRenderer.setSunColor(new Color(_weather.sun.noon)); break;
        }
    }

    public WeatherModel getWeather() {
        return _weather;
    }
}
