package org.smallbox.faraway.core.game;

import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.data.factory.world.WorldFactory;
import org.smallbox.faraway.core.data.serializer.GameSerializer;
import org.smallbox.faraway.core.engine.renderer.MainRenderer;
import org.smallbox.faraway.core.game.model.Data;
import org.smallbox.faraway.core.game.model.planet.RegionInfo;
import org.smallbox.faraway.core.game.module.path.PathManager;
import org.smallbox.faraway.core.game.module.world.WorldModule;
import org.smallbox.faraway.core.module.GameModule;
import org.smallbox.faraway.core.module.java.ModuleManager;
import org.smallbox.faraway.core.util.FileUtils;
import org.smallbox.faraway.core.util.Log;
import org.smallbox.faraway.ui.UserInterface;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

        Game game = new Game(info, Data.config);

        // TODO
        game.preload();
        game.init();

        startGame(game, saveInfo);

        _game = game;

        Log.notice("Load save (" + (System.currentTimeMillis() - time) + "ms)");
    }

    public void startGame(Game game, GameInfo.GameSaveInfo saveInfo) {
        long time = System.currentTimeMillis();
        MainRenderer.getInstance().init(Data.config, game);
        Log.notice("Init renderers (" + (System.currentTimeMillis() - time) + "ms)");

        game.setInputDirection(Application.getInstance().getInputProcessor().getDirection());

        if (saveInfo != null) {
            game.load(saveInfo);
        }

        Application.getInstance().notify(GameObserver::onGameStart);
        Application.getInstance().notify(observer -> observer.onHourChange(game.getHour()));
        Application.getInstance().notify(observer -> observer.onDayChange(game.getDay()));
        Application.getInstance().notify(observer -> observer.onYearChange(game.getYear()));
    }

    public void create(RegionInfo regionInfo) {
        long time = System.currentTimeMillis();

        GameInfo gameInfo = GameInfo.create(regionInfo, 300, 200);
        if (!new File("data/saves/", gameInfo.name).mkdirs()) {
            System.out.println("Unable to create game save directory");
            return;
        }

        Game game = new Game(gameInfo, Data.config);
        game.init();

        WorldModule world = (WorldModule) ModuleManager.getInstance().getModule(WorldModule.class);
        world.create();

        WorldFactory factory = new WorldFactory();
        factory.create(game, world, regionInfo);
        factory.createLandSite(game);

        startGame(game, null);

        _game = game;

        saveGame(gameInfo, GameInfo.Type.INIT);
        writeGameInfo(gameInfo);

        Log.notice("Create new game (" + (System.currentTimeMillis() - time) + "ms)");
    }

    private void writeGameInfo(GameInfo gameInfo) {
        try {
            FileOutputStream fos = new FileOutputStream(new File("data/saves/" + gameInfo.name, "game.json"));
            FileUtils.write(fos, gameInfo.toJSON().toString(4));
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveGame(GameInfo gameInfo, GameInfo.Type type) {
        if (_game != null) {
            GameInfo.GameSaveInfo saveInfo = new GameInfo.GameSaveInfo();
            saveInfo.type = type;
            saveInfo.date = new Date();
            saveInfo.label = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss").format(saveInfo.date);
            saveInfo.filename = gameInfo.name + "/" + new SimpleDateFormat("yyyy-MM-dd-hh-hh-mm-ss").format(saveInfo.date) + ".sav";
            gameInfo.saveFiles.add(saveInfo);
            GameSerializer.save(new File("data/saves/", saveInfo.filename));
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
