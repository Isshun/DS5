package org.smallbox.faraway.core.engine.module;

import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameObserver;
import org.smallbox.faraway.util.Log;

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
    protected int           _updateInterval = 1;
    private List<Long>      _updateTimeHistory = new ArrayList<>();
    private long            _updateTime;
    private int             _nbUpdate;
    private long            _totalTime;

    //    public void onGameCreateObserver(Game game) {}
    protected void onGameUpdate(Game game, int tick) {}

    public void onGameCreate(Game game) {}

    public void createGame(Game game) {
        Log.info("[" + _info.name + "] Create game");
//        if (runOnMainThread()) {
//            onGameCreateObserver(game);
//            _isLoaded = true;
//        } else {
//            Application.moduleManager.getExecutor().execute(() -> {
//                onGameCreateObserver(game);
//                _isLoaded = true;
//            });
//        }
        onGameCreate(game);
        _isLoaded = true;
    }

    public void startGame(Game game) {
        Log.info("[" + _info.name + "] Start game");
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

    public void updateGame(Game game, int tick) {
        onModuleUpdate(game);
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

    public void setUpdateInterval(int updateInterval) {
        _updateInterval = updateInterval;
    }
}
