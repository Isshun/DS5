package org.smallbox.faraway.core.game;

import com.badlogic.gdx.Input;
import org.smallbox.faraway.client.menu.MenuManager;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.GameShortcut;
import org.smallbox.faraway.core.dependencyInjector.DependencyInjector;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.AfterGameLayerInit;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.OnGameLayerInit;
import org.smallbox.faraway.core.game.save.*;
import org.smallbox.faraway.core.game.service.applicationConfig.ApplicationConfig;
import org.smallbox.faraway.core.module.path.PathManager;
import org.smallbox.faraway.modules.world.factory.WorldFactory;
import org.smallbox.faraway.util.FileUtils;
import org.smallbox.faraway.util.log.Log;

import java.io.File;

@ApplicationObject
public class GameManager implements GameObserver {

    @Inject
    private WorldFactory worldFactory;

    @Inject
    private GameSaveManager gameSaveManager;

    @Inject
    private GameLoadManager gameLoadManager;

    @Inject
    private ApplicationConfig applicationConfig;

    @Inject
    private PathManager pathManager;

    @Inject
    private GameFileManager gameFileManager;

    @Inject
    private MenuManager menuManager;

    @Inject
    private Data data;

    private Game _game;

    public interface GameListener {
        void onGameCreate(Game game);
        void onGameUpdate(Game game);
    }

    private static enum Mode {CREATE, LOAD}

    public void createGame(GameInfo gameInfo, GameListener listener) {
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
    }

    public void loadGame(GameInfo gameInfo, GameSaveInfo gameSaveInfo, GameListener listener) {
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
    }

    private void phase1(GameInfo gameInfo) {
        DependencyInjector.getInstance().destroyGameObjects();

        _game = new Game(gameInfo, applicationConfig);

        // For now game is created and register to DI manually because ctor need GameInfo
        DependencyInjector.getInstance().register(_game);
        DependencyInjector.getInstance().createGameObjects();

        _game.loadModules();
        _game.loadLayers();

        // Inject GameObjects
        DependencyInjector.getInstance().injectGameDependencies();
        DependencyInjector.getInstance().callMethodAnnotatedBy(OnGameLayerInit.class);
        DependencyInjector.getInstance().callMethodAnnotatedBy(AfterGameLayerInit.class);
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

        menuManager.setVisible(false);
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

    @GameShortcut(key = Input.Keys.F5)
    public void actionQuickSaveGame() {
        Log.info("quickSaveGame");
        gameSaveManager.saveGame(GameSaveType.FAST);
    }

}