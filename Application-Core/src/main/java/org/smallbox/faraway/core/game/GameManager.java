package org.smallbox.faraway.core.game;

import com.badlogic.gdx.Gdx;
import org.smallbox.faraway.client.shortcut.GameShortcut;
import org.smallbox.faraway.core.config.ApplicationConfig;
import org.smallbox.faraway.core.dependencyInjector.DependencyManager;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.AfterGameLayerInit;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.OnGameLayerInit;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.OnGameStart;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.OnGameStop;
import org.smallbox.faraway.core.save.*;
import org.smallbox.faraway.game.world.factory.WorldFactory;
import org.smallbox.faraway.util.FileUtils;
import org.smallbox.faraway.util.log.Log;

import java.io.File;
import java.util.Optional;

@ApplicationObject
public class GameManager implements GameObserver {
    @Inject private ApplicationConfig applicationConfig;
    @Inject private DependencyManager dependencyManager;
    @Inject private GameFileManager gameFileManager;
    @Inject private GameSaveManager gameSaveManager;
    @Inject private GameLoadManager gameLoadManager;
    @Inject private WorldFactory worldFactory;
    @Inject private Game game;

    public void newGame(GameInfo gameInfo, Runnable listener) {
        Gdx.app.postRunnable(() -> {
            long time = System.currentTimeMillis();

            File gameDirectory = FileUtils.getSaveDirectory(gameInfo.name);
            if (!gameDirectory.mkdirs()) {
                Log.info("Unable to createGame game onSave directory");
                return;
            }

            createGame(gameInfo);

            worldFactory.buildMap();

            Optional.ofNullable(listener).ifPresent(Runnable::run);

            dependencyManager.callMethodAnnotatedBy(OnGameStart.class);

            Log.info("New game (" + (System.currentTimeMillis() - time) + "ms)");
        });
    }

    public void loadGame(GameInfo gameInfo, GameSaveInfo gameSaveInfo, Runnable listener) {
        Gdx.app.postRunnable(() -> {
            try {
                long time = System.currentTimeMillis();

                createGame(gameInfo);

                gameLoadManager.load(FileUtils.getSaveDirectory(gameInfo.name), gameSaveInfo.filename, () -> {
                    Optional.ofNullable(listener).ifPresent(Runnable::run);

                    dependencyManager.callMethodAnnotatedBy(OnGameStart.class);

                    Log.info("Load game (" + (System.currentTimeMillis() - time) + "ms)");
                });

            } catch (Exception e) {
                Log.error(e);
            }
        });
    }

    private void createGame(GameInfo gameInfo) {
        // For now game is created and register to DI manually because ctor need GameInfo
        dependencyManager.register(new Game(gameInfo, applicationConfig));
        dependencyManager.createGameObjects();

        // Inject GameObjects
        dependencyManager.destroyNonBindControllers();
        dependencyManager.injectGameDependencies();
        dependencyManager.callMethodAnnotatedBy(OnGameLayerInit.class);
        dependencyManager.callMethodAnnotatedBy(AfterGameLayerInit.class);
    }

    public void destroyGame() {
        dependencyManager.callMethodAnnotatedBy(OnGameStop.class);
        dependencyManager.destroyGameObjects();
    }

    @Deprecated
    public boolean isLoaded() {
        return game != null && game.getStatus() == GameStatus.STARTED;
    }

    public boolean isRunning() {
        return game != null && game.isRunning();
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