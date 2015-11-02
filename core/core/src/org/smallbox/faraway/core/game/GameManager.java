package org.smallbox.faraway.core.game;

import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.data.factory.world.WorldFactory;
import org.smallbox.faraway.core.engine.renderer.MainRenderer;
import org.smallbox.faraway.core.game.model.GameData;
import org.smallbox.faraway.core.game.model.planet.RegionInfo;
import org.smallbox.faraway.core.game.module.path.PathManager;
import org.smallbox.faraway.core.game.module.world.WorldModule;
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

    public void load(String fileName) {
        long time = System.currentTimeMillis();

        _game = new Game(250, 250, GameData.getData(), GameData.config, fileName, null, null, null);

        // TODO
        _game.preload();

        startGame(true);

        Log.notice("Load save (" + (System.currentTimeMillis() - time) + "ms)");
    }

    public void startGame(boolean load) {
        long time = System.currentTimeMillis();
        MainRenderer.getInstance().init(GameData.config, _game);
        Log.notice("Init renderers (" + (System.currentTimeMillis() - time) + "ms)");

        time = System.currentTimeMillis();
        UserInterface.getInstance().setGame(_game);
        Log.notice("Create UI (" + (System.currentTimeMillis() - time) + "ms)");

//        if (_lightRenderer != null) {
//            time = System.currentTimeMillis();
//            _lightRenderer.init();
//            Log.notice("Init light (" + (System.currentTimeMillis() - time) + "ms)");
//        }

        _game.init(null);
        _game.setRegion(GameData.getData().getRegion("base.planet.arrakis", "desert"));
        _game.setInputDirection(Application.getInstance().getInputProcessor().getDirection());

        time = System.currentTimeMillis();
        PathManager.getInstance().init(Game.getInstance().getInfo().worldWidth, Game.getInstance().getInfo().worldHeight);
        Log.notice("Init paths (" + (System.currentTimeMillis() - time) + "ms)");

        if (load) {
            _game.load();
        }

        Game.getInstance().notify(GameObserver::onStartGame);
    }

    public void create(String fileName, RegionInfo regionInfo) {
        long time = System.currentTimeMillis();

        WorldFactory factory = new WorldFactory();

        _game = new Game(50, 50, GameData.getData(), GameData.config, fileName, null, null, regionInfo);
        _game.init(factory);

        WorldModule world = (WorldModule) ModuleManager.getInstance().getModule(WorldModule.class);
        world.create();
        factory.create(world, regionInfo);
        _game.save(fileName);

        factory.createLandSite(_game);

        startGame(false);

        Log.notice("Create new game (" + (System.currentTimeMillis() - time) + "ms)");
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
}
