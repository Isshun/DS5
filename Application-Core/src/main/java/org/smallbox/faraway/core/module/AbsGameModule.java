package org.smallbox.faraway.core.module;

import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameObserver;
import org.smallbox.faraway.util.log.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * AbsGameModule life-cycle
 *
 * Load
 * CreateGame
 * StartGame
 * UpdateGame (n)
 * StopGame
 * Unload
 */
public abstract class AbsGameModule extends ModuleBase implements GameObserver {
    @Inject private Game game;

    protected int           _updateInterval = 1;
    private final List<Long>      _updateTimeHistory = new ArrayList<>();
    private long            _updateTime;
    private int             _nbUpdate;
    private long            _totalTime;
    private long            _lastTick;
    private long            _tick;
    private long            _lastTime;
    private long            _cumulateTime;
    private long            _tickInterval;
    private double          _hourInterval;

    //    public void onGameInit(Game game) {}
    protected void onGameUpdate(Game game, int tick) {}

    public void onGameCreate(Game game) {}

    public void createGame(Game game) {
        Log.debug(getClass(), "Create game");
//        if (runOnMainThread()) {
//            onGameInit(game);
//            _isLoaded = true;
//        } else {
//            Application.moduleManager.getExecutor().execute(() -> {
//                onGameInit(game);
//                _isLoaded = true;
//            });
//        }
        onGameCreate(game);
        _isLoaded = true;
    }

    public void startGame(Game game) {
        Log.debug(getClass(), "Start game");
//        if (runOnMainThread()) {
//            onGameStart(game);
//            _isStarted = true;
//        } else {
//            Application.moduleManager.getExecutor().execute(() -> {
//                onGameStart(game);
//                _isStarted = true;
//            });
//        }
        onGameStart(game);
        _isStarted = true;
    }

    protected void onModuleUpdate(Game game) {}

    public void updateGame(Game game) {
        long time = System.nanoTime() / 1000;
        _tick = game.getTick();
        _tickInterval = _tick - _lastTick;
        _hourInterval = _tickInterval / game.getTickPerHour();
        onModuleUpdate(game);
        _lastTick = game.getTick();
        _lastTime = System.nanoTime() / 1000 - time;
        _cumulateTime += _lastTime;
//        if (_isStarted) {
//            if (tick % _updateInterval == 0) {
////                if (runOnMainThread()) {
////                    innerUpdate(game, tick);
////                } else {
////                    Application.moduleManager.getExecutor().execute(() -> onGameUpdate(game, tick));
////                }
//
//            }
//        }
    }

    private void innerUpdate(Game game, int tick) {
        if (tick % _updateInterval == 0) {
            long time = System.currentTimeMillis();
            onGameUpdate(game, tick);
            _updateTimeHistory.add((System.currentTimeMillis() - time));
            if (_updateTimeHistory.size() > 10) {
                _updateTimeHistory.remove(0);
            }
            _updateTime = 0;
            for (long t: _updateTimeHistory) {
                _updateTime += t;
            }
            _updateTime = _updateTime / _updateTimeHistory.size();
            _totalTime += (System.currentTimeMillis() - time);
            _nbUpdate++;
        }
    }

    public long         getModuleUpdateTime() { return _updateTime; }
    public long         getLastTime() { return _lastTime; }
    public long         getCumulateTime() { return _cumulateTime; }
    public long         getLastTick() { return _lastTick; }
    public long         getTick() { return _tick; }
    public long         getTickInterval() { return _tickInterval; }
    public double       getHourInterval() { return _hourInterval; }

    public void setUpdateInterval(int updateInterval) {
        _updateInterval = updateInterval;
    }
}
