package org.smallbox.faraway.core.game;

import com.badlogic.gdx.Input;
import org.json.JSONObject;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.GameShortcut;
import org.smallbox.faraway.core.dependencyInjector.DependencyInjector;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.AfterGameLayerInit;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.OnGameLayerInit;
import org.smallbox.faraway.core.game.service.applicationConfig.ApplicationConfigService;
import org.smallbox.faraway.modules.world.factory.WorldFactory;
import org.smallbox.faraway.util.FileUtils;
import org.smallbox.faraway.util.Log;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@ApplicationObject
public class GameManager implements GameObserver {

    @Inject
    private WorldFactory worldFactory;

    @Inject
    private GameSaveManager gameSaveManager;

    @Inject
    private ApplicationConfigService applicationConfigService;

    @Inject
    private Data data;

    private Game _game;

    public interface GameListener {
        void onGameCreate(Game game);
        void onGameUpdate(Game game);
    }

    public void createGame(GameInfo gameInfo, GameListener listener) {
        long time = System.currentTimeMillis();

        File gameDirectory = FileUtils.getSaveDirectory(gameInfo.name);
        if (!gameDirectory.mkdirs()) {
            Log.info("Unable to createGame game onSave directory");
            return;
        }

        _game = new Game(gameInfo, applicationConfigService.getConfig());
        DependencyInjector.getInstance().destroyGameObjects();

        // For now game is created and register to DI manually because ctor need GameInfo
        DependencyInjector.getInstance().register(_game);
        DependencyInjector.getInstance().createGameObjects();

        _game.loadModules();
        _game.loadLayers();

        // Inject GameObjects
        DependencyInjector.getInstance().injectGameDependencies();
        DependencyInjector.getInstance().callMethodAnnotatedBy(OnGameLayerInit.class);
        DependencyInjector.getInstance().callMethodAnnotatedBy(AfterGameLayerInit.class);

        worldFactory.buildMap();

        _game.createModules();

//        Application.runOnMainThread(() -> {

            if (listener != null) {
                listener.onGameCreate(_game);
            }
//        });

//        gameSaveManager.saveGame(_game, gameInfo, GameInfo.Type.INIT);

        // TODO: qui à la rsp de l'envoi des events ?
//        Application.runOnMainThread(() -> {
            Application.notify(observer -> observer.onGameStart(_game));
//        });

        _game.start();
        _game.getModules().forEach(module -> module.startGame(_game));

        Application.clientListener.onInitComplete();

        // Launch background thread
        _game.launchBackgroundThread(listener);

//        worldFactory.createLandSite(game);

        Log.notice("Create new game (" + (System.currentTimeMillis() - time) + "ms)");
    }

    public void loadGame(GameInfo gameInfo, GameInfo.GameSaveInfo gameSaveInfo, GameListener listener) {
        try {
            long time = System.currentTimeMillis();

            _game = new Game(gameInfo, applicationConfigService.getConfig());
            DependencyInjector.getInstance().register(_game);

            _game.loadModules();

            DependencyInjector.getInstance().injectGameDependencies();

            _game.createModules();

            if (listener != null) {
                listener.onGameCreate(_game);
            }

            gameSaveManager.load(_game, FileUtils.getSaveDirectory(gameInfo.name), gameSaveInfo.filename, () -> {

                Application.notify(observer -> observer.onGameStart(_game));

                _game.start();
                _game.getModules().forEach(module -> module.startGame(_game));

                Application.clientListener.onInitComplete();

                // Launch background thread
                _game.launchBackgroundThread(listener);

                Log.notice("Create new game (" + (System.currentTimeMillis() - time) + "ms)");

            });
        } catch (Exception e) {
            Log.error(e);
        }
    }

    public void closeGame() {
        _game.stop();
        _game = null;

        DependencyInjector.getInstance().destroyGameObjects();
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
                .min((o1, o2) -> o2.date.compareTo(o1.date))
                .ifPresent(saveInfo -> {
                    Log.info("Load save: " + saveInfo);
                    loadGame(saveInfo.game, saveInfo, null);
                });
    }

    @GameShortcut(key = Input.Keys.F5)
    public void actionQuickSaveGame() {
        Log.notice("quickSaveGame");
        gameSaveManager.saveGame(_game, _game.getInfo(), GameInfo.Type.FAST);
    }

}