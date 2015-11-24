package org.smallbox.faraway.core.game;

import com.badlogic.gdx.Gdx;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.game.module.world.factory.WorldFactory;
import org.smallbox.faraway.core.data.serializer.GameSaveManager;
import org.smallbox.faraway.core.engine.module.java.ModuleManager;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.model.planet.RegionInfo;
import org.smallbox.faraway.core.util.FileUtils;
import org.smallbox.faraway.core.util.Log;

import java.io.File;
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
        Application.getInstance().notify(GameObserver::onReloadUI);
        Game game = new Game(info);
        GameSaveManager.load(game, FileUtils.getSaveDirectory(game.getInfo().name), saveInfo.filename, () -> Gdx.app.postRunnable(() -> {
            System.gc();
            ModuleManager.getInstance().startGame(game);
            game.init();
            _game = game;
        }));
    }

    public void create(RegionInfo regionInfo) {
        long time = System.currentTimeMillis();

        Application.getInstance().notify(GameObserver::onReloadUI);

        GameInfo gameInfo = GameInfo.create(regionInfo, 256, 160, 8);
        File gameDirectory = FileUtils.getSaveDirectory(gameInfo.name);
        if (!gameDirectory.mkdirs()) {
            Log.info("Unable to create game save directory");
            return;
        }

        Game game = new Game(gameInfo);
        WorldFactory factory = new WorldFactory();
        factory.create(game, regionInfo);

        WorldHelper.init(game.getInfo(), factory.getParcels());
        ModuleManager.getInstance().startGame(game);
        factory.createLandSite(game);
        game.init();
        _game = game;

//        saveGame(gameInfo, GameInfo.Type.INIT);

        writeGameInfo(gameInfo);

        Log.notice("Create new game (" + (System.currentTimeMillis() - time) + "ms)");
    }

    private void writeGameInfo(GameInfo gameInfo) {
        try {
            FileUtils.write(new File(FileUtils.getSaveDirectory(gameInfo.name), "game.json"), gameInfo.toJSON().toString(4));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveGame(GameInfo gameInfo, GameInfo.Type type) {
        if (_game != null) {
            Date date = new Date();
            String filename = new SimpleDateFormat("yyyy-MM-dd-hh-hh-mm-ss").format(date);
            File gameDirectory = new File("saves/", gameInfo.name);

            GameInfo.GameSaveInfo saveInfo = new GameInfo.GameSaveInfo();
            saveInfo.game = gameInfo;
            saveInfo.type = type;
            saveInfo.date = date;
            saveInfo.label = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss").format(saveInfo.date);
            saveInfo.filename = new SimpleDateFormat("yyyy-MM-dd-hh-hh-mm-ss").format(saveInfo.date);
            gameInfo.saveFiles.add(saveInfo);
            writeGameInfo(gameInfo);

            GameSaveManager.save(gameDirectory, filename);
        }
    }

    public boolean isLoaded() {
//        return _game != null && !_game.isLoaded();
        return _game != null;
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