package org.smallbox.faraway.core.game;

import org.smallbox.faraway.core.config.ApplicationConfig;
import org.smallbox.faraway.core.dependencyInjector.DependencyManager;
import org.smallbox.faraway.core.dependencyInjector.DependencyNotifier;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotation.callback.gameEvent.*;
import org.smallbox.faraway.core.save.GameInfo;
import org.smallbox.faraway.game.planet.PlanetInfo;
import org.smallbox.faraway.game.planet.PlanetModel;
import org.smallbox.faraway.game.planet.RegionInfo;
import org.smallbox.faraway.util.Utils;

@GameObject
public class Game {
    @Inject private DependencyManager dependencyManager;
    @Inject private DependencyNotifier dependencyNotifier;
    @Inject private GameTime gameTime;

    private final ApplicationConfig config;
    private double _tickPerHour;
    private RegionInfo.RegionMonth currentMonth;
    private long _nextUpdate;
    private final GameInfo _info;
    private final PlanetModel _planet;
    private boolean _isRunning;
    private int _speed;
    private int _lastSpeed;
    private GameStatus _status = GameStatus.UNINITIALIZED;
    private final PlanetInfo _planetInfo;
    private final RegionInfo _regionInfo;

    public Game(GameInfo info, ApplicationConfig config) {
        this.config = config;
        _speed = config.game.startSpeed;
        _lastSpeed = config.game.startSpeed;
        _tickPerHour = config.game.ticksPerHour[_speed];
        _planetInfo = info.planet;
        _regionInfo = info.region;
        _planet = info.planet != null ? new PlanetModel(info.planet) : null;
        _isRunning = true;
        _info = info;
    }

    @OnGameStart
    private void onGameStart() {
        _status = GameStatus.STARTED;
    }

    @OnGameStop
    public void onGameStop() {
        _status = GameStatus.STOPPED;
    }

    @OnGameUpdate
    private void onGameUpdate() {
        int hour = gameTime.getHour();
        int day = gameTime.getDay();
        int month = gameTime.getMonth();

        gameTime.add(1 / _tickPerHour);

        if (gameTime.getMonth() > month) {
            currentMonth = _regionInfo.months.stream().filter(m -> m.index == gameTime.getMonth()).findFirst().orElse(null);
            dependencyNotifier.notify(OnGameNewMonth.class);
        }

        if (gameTime.getDay() > day) {
            dependencyNotifier.notify(OnGameNewDay.class);
        }

        if (gameTime.getHour() > hour) {
            dependencyNotifier.notify(OnGameNewHour.class);
        }
    }

    public boolean isRunning() {
        return _isRunning;
    }

    public void toggleRunning() {
        setRunning(!isRunning());
    }

    public void setRunning(boolean running) {
        _isRunning = running;
    }

    public double getTickPerHour() {
        return _tickPerHour;
    }

    public int getHourPerDay() {
        return _planet.getInfo().dayDuration;
    }

    public PlanetModel getPlanet() {
        return _planet;
    }

    public PlanetInfo getPlanetInfo() {
        return _planetInfo;
    }

    public RegionInfo getRegionInfo() {
        return _regionInfo;
    }

    public int getSpeed() {
        return _speed;
    }

    public GameStatus getStatus() {
        return _status;
    }

    public long getNextUpdate() {
        return _nextUpdate;
    }

    public RegionInfo.RegionMonth getCurrentMonth() {
        return currentMonth;
    }

    public GameInfo getInfo() {
        return _info;
    }

    public void setSpeed(int speed) {
        _lastSpeed = _speed;
        _speed = Utils.bound(1, config.game.ticksPerHour.length - 1, speed);
        if (_speed != _lastSpeed) {
//            _tickInterval = Application.config.game.ticksIntervals[_speed];
            _tickPerHour = config.game.ticksPerHour[_speed];
            _isRunning = speed > 0;
        }
    }

}
