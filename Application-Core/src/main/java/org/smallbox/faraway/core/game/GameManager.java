package org.smallbox.faraway.core.game;

import com.badlogic.gdx.Gdx;
import org.smallbox.faraway.client.shortcut.GameShortcut;
import org.smallbox.faraway.client.ui.UIManager;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.config.ApplicationConfig;
import org.smallbox.faraway.core.dependencyInjector.DependencyManager;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.AfterGameLayerInit;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.OnGameLayerInit;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.OnGameStart;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.OnGameStop;
import org.smallbox.faraway.core.path.PathManager;
import org.smallbox.faraway.core.save.*;
import org.smallbox.faraway.game.world.factory.WorldFactory;
import org.smallbox.faraway.util.FileUtils;
import org.smallbox.faraway.util.log.Log;

import java.io.File;

@ApplicationObject
public class GameManager implements GameObserver {
    @Inject private WorldFactory worldFactory;
    @Inject private GameSaveManager gameSaveManager;
    @Inject private GameLoadManager gameLoadManager;
    @Inject private ApplicationConfig applicationConfig;
    @Inject private PathManager pathManager;
    @Inject private GameFileManager gameFileManager;
    @Inject private DependencyManager dependencyManager;
    @Inject private DataManager dataManager;
    @Inject private UIManager uiManager;

    private Game _game;

    public interface GameListener {
        void onGameCreate(Game game);
        void onGameUpdate(Game game);
    }

    public void createGame(GameInfo gameInfo, GameListener listener) {
        Gdx.app.postRunnable(() -> {
            long time = System.currentTimeMillis();

            File gameDirectory = FileUtils.getSaveDirectory(gameInfo.name);
            if (!gameDirectory.mkdirs()) {
                Log.info("Unable to createGame game onSave directory");
                return;
            }

            phase1(gameInfo);
            worldFactory.buildMap();
//        worldFactory.createLandSite(game);
//        gameSaveManager.saveGame(_game, gameInfo, GameInfo.Type.INIT);
            phase2(listener);

            Log.info("Create new game (" + (System.currentTimeMillis() - time) + "ms)");
        });
    }

    public void loadGame(GameInfo gameInfo, GameSaveInfo gameSaveInfo, GameListener listener) {
        Gdx.app.postRunnable(() -> {
            try {
                long time = System.currentTimeMillis();

                phase1(gameInfo);

                gameLoadManager.load(FileUtils.getSaveDirectory(gameInfo.name), gameSaveInfo.filename, () -> {
                    phase2(listener);
                    Log.info("Load game (" + (System.currentTimeMillis() - time) + "ms)");
                });

            } catch (Exception e) {
                Log.error(e);
            }
        });
    }

    private void phase1(GameInfo gameInfo) {
        // Close previous game if exists (destroy GameObjects in DI)
        closeGame();

//        uiManager.clearViews();
//        uiManager.reloadViews();
//        uiManager.refreshApplication();

        _game = new Game(gameInfo, applicationConfig);

        // For now game is created and register to DI manually because ctor need GameInfo
        dependencyManager.register(_game);
        dependencyManager.createGameObjects();

        _game.loadModules();
        _game.loadLayers();

        // Inject GameObjects
        dependencyManager.destroyNonBindControllers();
        dependencyManager.injectGameDependencies();
        dependencyManager.callMethodAnnotatedBy(OnGameLayerInit.class);
        dependencyManager.callMethodAnnotatedBy(AfterGameLayerInit.class);
    }

    private void phase2(GameListener listener) {
        _game.createModules();

        if (listener != null) {
            listener.onGameCreate(_game);
        }

        pathManager.initParcels();

        Application.notify(observer -> observer.onGameStart(_game));

        _game.start();
        _game.getModules().forEach(module -> module.startGame(_game));

        // Launch background thread
        _game.launchBackgroundThread(listener);

        dependencyManager.callMethodAnnotatedBy(OnGameStart.class);
    }

    public void closeGame() {
        if (_game != null) {
            _game.stop();
            _game = null;

            dependencyManager.callMethodAnnotatedBy(OnGameStop.class);
            dependencyManager.destroyGameObjects();
        }
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

    public boolean isRunning() {
        return _game != null && _game.isRunning();
    }

    public void loadLastGame() {
        gameFileManager.buildGameList().stream()
                .flatMap(gameInfo -> gameInfo.saveFiles.stream())
                .min((o1, o2) -> o2.date.compareTo(o1.date))
                .ifPresent(saveInfo -> {
                    Log.info("Load save: " + saveInfo);
                    loadGame(saveInfo.game, saveInfo, null);
                });
    }

    @GameShortcut("game/save")
    public void actionQuickSaveGame() {
        Log.info("quickSaveGame");
        gameSaveManager.saveGame(GameSaveType.FAST);
    }

}