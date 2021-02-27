package org.smallbox.faraway.core.game;

import org.smallbox.faraway.client.shortcut.GameShortcut;
import org.smallbox.faraway.core.config.ApplicationConfig;
import org.smallbox.faraway.core.dependencyInjector.DependencyManager;
import org.smallbox.faraway.core.dependencyInjector.DependencyNotifier;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotation.callback.applicationEvent.OnApplicationLoadGame;
import org.smallbox.faraway.core.dependencyInjector.annotation.callback.applicationEvent.OnApplicationNewGame;
import org.smallbox.faraway.core.dependencyInjector.annotation.callback.gameEvent.OnGameLayerBegin;
import org.smallbox.faraway.core.dependencyInjector.annotation.callback.gameEvent.OnGameLayerComplete;
import org.smallbox.faraway.core.dependencyInjector.annotation.callback.gameEvent.OnGameStop;
import org.smallbox.faraway.core.save.*;
import org.smallbox.faraway.util.log.Log;

@ApplicationObject
public class GameManager {
    @Inject private ApplicationConfig applicationConfig;
    @Inject private DependencyManager dependencyManager;
    @Inject private DependencyNotifier dependencyNotifier;
    @Inject private GameSaveManager gameSaveManager;
    @Inject private GameLoadManager gameLoadManager;
    @Inject private Game game;

    public void newGame(GameInfo gameInfo, GameScenario scenario) {
        createGame(gameInfo);
        dependencyNotifier.notify(OnApplicationNewGame.class, scenario);
    }

    public void loadGame(GameInfo gameInfo, GameSaveInfo gameSaveInfo) {
        createGame(gameInfo);
        dependencyNotifier.notify(OnApplicationLoadGame.class, gameSaveInfo);
    }

    /**
     * Create game objects and call dependency injector
     */
    private void createGame(GameInfo gameInfo) {
        // For now game is created and register to DI manually because ctor need GameInfo
        dependencyManager.register(new Game(gameInfo, applicationConfig));
        dependencyManager.createGameObjects();

        // Inject GameObjects
        dependencyManager.destroyNonBindControllers();
        dependencyManager.injectGameDependencies();
        dependencyNotifier.notify(OnGameLayerBegin.class);
        dependencyNotifier.notify(OnGameLayerComplete.class);
    }

    public void destroyGame() {
        dependencyNotifier.notify(OnGameStop.class);
        dependencyManager.destroyGameObjects();
    }

    @Deprecated
    public boolean isLoaded() {
        return game != null && game.getStatus() == GameStatus.STARTED;
    }

    public boolean isRunning() {
        return game != null && game.isRunning();
    }

    @GameShortcut("game/save")
    public void actionQuickSaveGame() {
        Log.info("quickSaveGame");
        gameSaveManager.saveGame(GameSaveType.FAST);
    }

}