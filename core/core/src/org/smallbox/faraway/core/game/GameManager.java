package org.smallbox.faraway.core.game;

import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.data.factory.world.WorldFactory;
import org.smallbox.faraway.core.engine.renderer.MainRenderer;
import org.smallbox.faraway.core.game.model.Data;
import org.smallbox.faraway.core.game.model.planet.RegionInfo;
import org.smallbox.faraway.core.game.module.path.PathManager;
import org.smallbox.faraway.core.game.module.world.WorldModule;
import org.smallbox.faraway.core.module.GameModule;
import org.smallbox.faraway.core.module.java.ModuleManager;
import org.smallbox.faraway.core.util.Log;
import org.smallbox.faraway.ui.UserInterface;

/**
 * Created by Alex on 20/10/2015.
 */
public class GameManager {
    private static GameManager  _self;
    private Game                _game;

    public static GameManager getInstance() {
        if (_self == null) {
            _self = new GameManager();
        }
        return _self;
    }

    public void loadGame(GameInfo info, GameInfo.GameSaveInfo saveInfo) {
        long time = System.currentTimeMillis();

        Game game = new Game(info, 250, 250, Data.getData(), Data.config);

        // TODO
        game.preload();
        game.init(null);

        startGame(game, saveInfo);

        _game = game;

        Log.notice("Load save (" + (System.currentTimeMillis() - time) + "ms)");
    }

    public void startGame(Game game, GameInfo.GameSaveInfo saveInfo) {
        long time = System.currentTimeMillis();
        MainRenderer.getInstance().init(Data.config, game);
        Log.notice("Init renderers (" + (System.currentTimeMillis() - time) + "ms)");

        time = System.currentTimeMillis();
        UserInterface.getInstance().setGame(game);
        Log.notice("Create UI (" + (System.currentTimeMillis() - time) + "ms)");

//        if (_lightRenderer != null) {
//            time = System.currentTimeMillis();
//            _lightRenderer.init();
//            Log.notice("Init light (" + (System.currentTimeMillis() - time) + "ms)");
//        }

        game.setInputDirection(Application.getInstance().getInputProcessor().getDirection());

        time = System.currentTimeMillis();
        PathManager.getInstance().init(Game.getInstance().getInfo().worldWidth, Game.getInstance().getInfo().worldHeight);
        Log.notice("Init paths (" + (System.currentTimeMillis() - time) + "ms)");

        if (saveInfo != null) {
            game.load(saveInfo);
        }

        Application.getInstance().notify(GameObserver::onGameStart);
        Application.getInstance().notify(observer -> observer.onHourChange(game.getHour()));
        Application.getInstance().notify(observer -> observer.onDayChange(game.getDay()));
        Application.getInstance().notify(observer -> observer.onYearChange(game.getYear()));
    }

    public void create(String fileName, RegionInfo regionInfo) {
        long time = System.currentTimeMillis();

        WorldFactory factory = new WorldFactory();

        Game game = new Game(GameInfo.create(regionInfo), 50, 50, Data.getData(), Data.config);
        game.init(factory);

        WorldModule world = (WorldModule) ModuleManager.getInstance().getModule(WorldModule.class);
        world.create();
        factory.create(world, regionInfo);
//        game.save("base_1", fileName);

        factory.createLandSite(game);

        startGame(game, null);

        _game = game;

        saveGame();

        Log.notice("Create new game (" + (System.currentTimeMillis() - time) + "ms)");
    }

    public void saveGame() {
        if (_game != null) {
            _game.save("base_1", "14.sav");
        }
    }

    public boolean isRunning() {
//        return _game != null && !_game.isRunning();
        return _game != null;
    }

    public Game getGame() {
        return _game;
    }

    public boolean isPaused() {
        return _game != null && _game.isPaused();
    }

    public void setPause(boolean pause) {
        if (_game != null) {
            _game.setPaused(pause);
        }
    }

    public void stopGame() {
        _game = null;
    }
}
