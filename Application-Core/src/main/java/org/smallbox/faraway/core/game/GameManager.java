package org.smallbox.faraway.core.game;

import com.badlogic.gdx.Gdx;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.game.model.planet.RegionInfo;
import org.smallbox.faraway.core.module.IWorldFactory;
import org.smallbox.faraway.util.FileUtils;
import org.smallbox.faraway.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Alex on 20/10/2015.
 */
public class GameManager {
    private Game                _game;

    @BindModule
    private IWorldFactory       worldFactory;

    public void loadGame(GameInfo info, GameInfo.GameSaveInfo saveInfo) {
//        Application.notify(GameObserver::onReloadUI);
        _game = new Game(info);
        _game.createModules();

        Application.notify(observer -> observer.onGameCreate(_game));

        GameSaveManager.load(_game, FileUtils.getSaveDirectory(info.name), saveInfo.filename, () -> Gdx.app.postRunnable(() -> {
            System.gc();
            Application.luaModuleManager.startGame(_game);
            _game.start();
        }));
    }

    public void createGame(RegionInfo regionInfo) {
        long time = System.currentTimeMillis();

        GameInfo gameInfo = GameInfo.create(regionInfo, 256, 160, 8);
        File gameDirectory = FileUtils.getSaveDirectory(gameInfo.name);
        if (!gameDirectory.mkdirs()) {
            Log.info("Unable to createGame game onSave directory");
            return;
        }

        _game = new Game(gameInfo);
        _game.createModules();

        worldFactory.create(_game, regionInfo);

        saveGame(gameInfo, GameInfo.Type.INIT);

        _game.start();
//        worldFactory.createLandSite(game);



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
        if (_game == null) {
            throw new RuntimeException("Game cannot be null");
        }

        Date date = new Date();
        String filename = new SimpleDateFormat("yyyy-MM-dd-hh-hh-mm-ss").format(date);
        File gameDirectory = FileUtils.getSaveDirectory(gameInfo.name);

        GameInfo.GameSaveInfo saveInfo = new GameInfo.GameSaveInfo();
        saveInfo.game = gameInfo;
        saveInfo.type = type;
        saveInfo.date = date;
        saveInfo.label = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss").format(saveInfo.date);
        saveInfo.filename = new SimpleDateFormat("yyyy-MM-dd-hh-hh-mm-ss").format(saveInfo.date);
        gameInfo.saveFiles.add(saveInfo);
        writeGameInfo(gameInfo);

        GameSaveManager.save(_game, gameDirectory, filename);
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