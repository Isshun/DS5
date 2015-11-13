package org.smallbox.faraway.core.game;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;
import com.badlogic.gdx.Gdx;
import org.json.JSONObject;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.data.factory.world.WorldFactory;
import org.smallbox.faraway.core.data.serializer.GameSerializer;
import org.smallbox.faraway.core.data.serializer.GameSerializer.GameSerializerInterface;
import org.smallbox.faraway.core.engine.renderer.MainRenderer;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.model.Data;
import org.smallbox.faraway.core.game.model.planet.RegionInfo;
import org.smallbox.faraway.core.game.module.path.PathManager;
import org.smallbox.faraway.core.game.module.world.SQLHelper;
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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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
        game.load(game.getInfo(), saveInfo, () -> Gdx.app.postRunnable(() -> {
            System.gc();
            game.init();
            startGame(game, saveInfo);
            _game = game;
            Log.notice("Load save (" + (System.currentTimeMillis() - time) + "ms)");
        }));
    }

    public void startGame(Game game, GameInfo.GameSaveInfo saveInfo) {
        long time = System.currentTimeMillis();
        MainRenderer.getInstance().init(Data.config, game);
        Log.notice("Init renderers (" + (System.currentTimeMillis() - time) + "ms)");

        game.setInputDirection(Application.getInstance().getInputProcessor().getDirection());

        Application.getInstance().notify(GameObserver::onGameStart);
        Application.getInstance().notify(observer -> observer.onHourChange(game.getHour()));
        Application.getInstance().notify(observer -> observer.onDayChange(game.getDay()));
        Application.getInstance().notify(observer -> observer.onYearChange(game.getYear()));
    }

    public void create(RegionInfo regionInfo) {
        long time = System.currentTimeMillis();

        GameInfo gameInfo = GameInfo.create(regionInfo, 256, 256, 32);
        File gameDirectory = new File("data/saves/", gameInfo.name);

        if (!gameDirectory.mkdirs()) {
            System.out.println("Unable to create game save directory");
            return;
        }

        Game game = new Game(gameInfo, Data.config);
        WorldModule world = (WorldModule) ModuleManager.getInstance().getModule(WorldModule.class);

        WorldFactory factory = new WorldFactory();
        factory.create(game, world, regionInfo);

        WorldHelper.init(factory.getParcels(), game.getInfo().worldFloors - 1);
        game.init();

        factory.createLandSite(game);

//        world.create();

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
            Date date = new Date();
            String filename = new SimpleDateFormat("yyyy-MM-dd-hh-hh-mm-ss").format(date);
            File gameDirectory = new File("data/saves/", gameInfo.name);

            GameInfo.GameSaveInfo saveInfo = new GameInfo.GameSaveInfo();
            saveInfo.type = type;
            saveInfo.date = date;
            saveInfo.label = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss").format(saveInfo.date);
            saveInfo.filename = new SimpleDateFormat("yyyy-MM-dd-hh-hh-mm-ss").format(saveInfo.date);
            gameInfo.saveFiles.add(saveInfo);
            try {
                FileOutputStream fos = new FileOutputStream(new File(gameDirectory, "game.json"));
                FileUtils.write(fos, gameInfo.toJSON().toString(4));
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            GameSerializer.save(gameDirectory, filename);
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