package org.smallbox.faraway.game.weather;

import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotation.callback.gameEvent.OnGameNewDay;
import org.smallbox.faraway.core.dependencyInjector.annotation.callback.gameEvent.OnGameNewHour;
import org.smallbox.faraway.core.dependencyInjector.annotation.callback.gameEvent.OnGameNewMonth;
import org.smallbox.faraway.core.dependencyInjector.annotation.callback.gameEvent.OnGameUpdate;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameTime;
import org.smallbox.faraway.core.module.SuperGameModule2;
import org.smallbox.faraway.util.Random;
import org.smallbox.faraway.util.transition.DoubleTransition;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@GameObject
public class WorldTemperatureModule extends SuperGameModule2<WeatherModuleObserver> {
    @Inject private WeatherModule weatherModule;
    @Inject private GameTime gameTime;
    @Inject private Game game;

    private DoubleTransition temperatureTransition;
    private double weatherTemperatureVariation;
    private double dailyTemperatureVariation;
    private double hourlyTemperatureVariation;
    private double monthlyTemperature;
    private double temperature;

    @OnGameNewHour
    public void refreshHourlyTemperatureVariation() {
        Optional.ofNullable(game.getCurrentMonth()).ifPresent(currentMonth -> {
            hourlyTemperatureVariation = currentMonth.temperatureHourlyVariations[gameTime.getHour()];
            refreshTemperatureTransition();
        });
    }

    @OnGameNewDay
    public void refreshDailyTemperatureVariation() {
        Optional.ofNullable(game.getCurrentMonth()).ifPresent(currentMonth -> dailyTemperatureVariation += Random.gaussianInterval(5));
    }

    @OnGameNewMonth
    public void refreshMonthlyTemperature() {
        Optional.ofNullable(game.getCurrentMonth()).ifPresent(currentMonth -> monthlyTemperature = Random.interval(currentMonth.temperature));
    }

    @OnGameUpdate
    public void onGameUpdate() {

        // Refresh temperatureTransition if weather has changed since last update
        if (weatherTemperatureVariation != weatherModule.getTemperatureVariation()) {
            weatherTemperatureVariation = weatherModule.getTemperatureVariation();
            refreshTemperatureTransition();
        }

        // Refresh temperature based on temperatureTransition
        if (temperatureTransition != null) {
            temperature = temperatureTransition.getValue(gameTime.now());
        }

    }

    private void refreshTemperatureTransition() {
        temperatureTransition = new DoubleTransition(temperature, monthlyTemperature + dailyTemperatureVariation + hourlyTemperatureVariation + weatherModule.getTemperatureVariation());
        temperatureTransition.setInterval(gameTime.now(), gameTime.plus(1, TimeUnit.HOURS));
    }

    public double getTemperature() {
        return temperature;
    }

}