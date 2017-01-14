package org.smallbox.faraway.core.game;

import com.badlogic.gdx.Gdx;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.config.Config;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.core.game.model.planet.RegionInfo;
import org.smallbox.faraway.core.module.IWorldFactory;
import org.smallbox.faraway.util.FileUtils;
import org.smallbox.faraway.util.Log;

import java.io.File;

/**
 * Created by Alex on 20/10/2015.
 */
public class GameManager implements GameObserver {
    private Game                _game;

    @BindModule
    private IWorldFactory       worldFactory;
    private boolean _paused;

    @Override
    public void onGameLoad(GameInfo gameInfo, GameInfo.GameSaveInfo gameSaveInfo) {
//        Application.notify(GameObserver::onReloadUI);
        _game = new Game(gameInfo);
        _game.createModules();

        Gdx.app.postRunnable(() -> Application.notify(observer -> observer.onGameCreate(_game)));

        Application.gameSaveManager.load(_game, FileUtils.getSaveDirectory(gameInfo.name), gameSaveInfo.filename,
                () -> Gdx.app.postRunnable(() -> {
                    Application.notify(observer -> observer.onGameStart(_game));
                    _game.start();
                }));
    }

    @Override
    public void onKeyEvent(GameEventListener.Action action, GameEventListener.Key key, GameEventListener.Modifier modifier) {
        if (action == GameEventListener.Action.RELEASED && key == GameEventListener.Key.P) {
            _paused = !_paused;
        }
    }

    @Override
    public void onGameUpdate(Game game) {
        if (!_paused) {
            _game.update();
        }
    }

    public void createGame(RegionInfo regionInfo) {
        long time = System.currentTimeMillis();

        GameInfo gameInfo = GameInfo.create(regionInfo, 32, 32, Config.FLOOR + 1);
        File gameDirectory = FileUtils.getSaveDirectory(gameInfo.name);
        if (!gameDirectory.mkdirs()) {
            Log.info("Unable to createGame game onSave directory");
            return;
        }

        _game = new Game(gameInfo);
        _game.createModules();

        worldFactory.create(_game, regionInfo);

        Application.gameSaveManager.saveGame(_game, gameInfo, GameInfo.Type.INIT);

        _game.start();
//        worldFactory.createLandSite(game);

        Log.notice("Create new game (" + (System.currentTimeMillis() - time) + "ms)");
    }

    public boolean isLoaded() {
        return _game != null && _game.getState() == Game.GameModuleState.STARTED;
    }

    public Game getGame() {
        return _game;
    }

    public boolean isRunning() {
        return _game != null && _game.isRunning();
    }

    public void setRunning(boolean pause) {
        if (_game != null) {
            _game.setRunning(pause);
        }
    }

    public void stopGame() {
        _game = null;
    }
}