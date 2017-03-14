package org.smallbox.faraway.core.game;

import org.json.JSONObject;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.GameShortcut;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.core.module.IWorldFactory;
import org.smallbox.faraway.modules.world.WorldModule;
import org.smallbox.faraway.util.FileUtils;
import org.smallbox.faraway.util.Log;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
        Application.dependencyInjector.register(_game);

        _game.createModules();

        Application.runOnMainThread(() -> Application.notify(observer -> observer.onGameCreateObserver(_game)));

        Application.gameSaveManager.load(_game, FileUtils.getSaveDirectory(gameInfo.name), gameSaveInfo.filename,
                () -> Application.runOnMainThread(() -> {
                    Application.notify(observer -> observer.onGameStart(_game));
                    _game.start();
                }));
    }

//    @Override
//    public void onKeyEvent(GameEventListener.Action action, GameEventListener.Key key, GameEventListener.Modifier modifier) {
//        if (action == GameEventListener.Action.RELEASED && key == GameEventListener.Key.P) {
//            _paused = !_paused;
//        }
//    }

    public interface GameCreateListener {
        void onGameCreate(Game game);
    }

    public interface GameUpdateListener {
        void onGameUpdate(Game game);
    }

    public interface GameListener {
        void onGameCreate(Game game);
        void onGameUpdate(Game game);
    }

    public void createGame(String planetName, String regionName, int worldWidth, int worldHeight, int worldFloors, GameListener listener) {
        createGame(GameInfo.create(planetName, regionName, worldWidth, worldHeight, worldFloors), listener);
    }

    public void createGame(GameInfo gameInfo, GameListener listener) {
        long time = System.currentTimeMillis();

        File gameDirectory = FileUtils.getSaveDirectory(gameInfo.name);
        if (!gameDirectory.mkdirs()) {
            Log.info("Unable to createGame game onSave directory");
            return;
        }

        _game = new Game(gameInfo);
        Application.dependencyInjector.register(_game);

        _game.loadModules();

        worldFactory.create(
                _game,
                _game.getModule(WorldModule.class),
                gameInfo.region);

        _game.createModules();

//        Application.runOnMainThread(() -> {
            Application.notify(observer -> observer.onGameCreateObserver(_game));
            if (listener != null) {
                listener.onGameCreate(_game);
            }
//        });

//        Application.gameSaveManager.saveGame(_game, gameInfo, GameInfo.Type.INIT);

        // TODO: qui à la rsp de l'envoi des events ?
//        Application.runOnMainThread(() -> {
            Application.notify(observer -> observer.onGameStart(_game));
//        });

        _game.start();
        _game.getModules().forEach(module -> module.startGame(_game));

        // Launch background thread
        _game.launchBackgroundThread(listener);

//        worldFactory.createLandSite(game);

        Log.notice("Create new game (" + (System.currentTimeMillis() - time) + "ms)");
    }

    /**
     * @return true si une partie existe
     */
    @Deprecated
    public boolean isLoaded() {
        return _game != null && _game.getState() == Game.GameStatus.STARTED;
    }

    /**
     * @return État de la partie si celle-ci existe
     */
    public Game.GameStatus getGameStatus() {
        return _game != null ? _game.getState() : null;
    }

    public Game getGame() {
        return _game;
    }

    public boolean isRunning() {
        return _game != null && _game.isRunning();
    }

    private List<GameInfo> buildGameList() {
        return FileUtils.list(FileUtils.getSaveDirectory()).stream()
                .filter(File::isDirectory)
                .map(gameDirectory -> {
                    File file = new File(gameDirectory, "game.json");
                    if (file.exists()) {
                        try {
                            Log.info("Load game directory: " + gameDirectory.getName());
                            return GameInfo.fromJSON(new JSONObject(new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8)));
                        } catch (IOException e) {
                            Log.warning("Cannot load gameInfo for: " + file.getAbsolutePath());
                        }
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public void loadLastGame() {
        buildGameList().stream()
                .flatMap(gameInfo -> gameInfo.saveFiles.stream())
                .sorted((o1, o2) -> o2.date.compareTo(o1.date))
                .findFirst()
                .ifPresent(saveInfo -> {
                    Log.info("Load save: " + saveInfo);
                    Application.notify(observer -> observer.onGameLoad(saveInfo.game, saveInfo));
                });
    }

    @GameShortcut(key = GameEventListener.Key.F5)
    public void actionQuickSaveGame() {
        Log.notice("quickSaveGame");
        Application.gameSaveManager.saveGame(
                Application.gameManager.getGame(),
                Application.gameManager.getGame().getInfo(),
                GameInfo.Type.FAST);
    }

    @GameShortcut(key = GameEventListener.Key.SPACE)
    public void actionPause() {
        _game.toggleRunning();
    }

    @GameShortcut(key = GameEventListener.Key.PLUS)
    public void actionSpeedUp() {
        _game.setSpeed(_game.getSpeed() + 1);
    }

    @GameShortcut(key = GameEventListener.Key.MINUS)
    public void actionSpeedDown() {
        _game.setSpeed(_game.getSpeed() - 1);
    }

}