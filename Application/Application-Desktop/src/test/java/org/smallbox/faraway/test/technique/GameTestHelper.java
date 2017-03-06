package org.smallbox.faraway.test.technique;

import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alex on 05/03/2017.
 */
public class GameTestHelper {

    private final TestBase _testBase;

    public GameTestHelper(TestBase testBase) {
        _testBase = testBase;
    }

    private Map<Long, Runnable> tickRunnable = new HashMap<>();
    private Runnable createRunnable;
    private long endTick;

    public static GameTestHelper create(TestBase testBase) {
        return new GameTestHelper(testBase);
    }

    public GameTestHelper runOnGameCreate(Runnable runnable) {
        createRunnable = runnable;
        return this;
    }

    public GameTestHelper runOnGameTick(long tick, Runnable runnable) {
        tickRunnable.put(tick, runnable);
        if (endTick < tick) {
            endTick = tick;
        }
        return this;
    }

    public void runUntil(int tick) throws InterruptedException {
        endTick = tick;
        doRun();
    }

    public void run() throws InterruptedException {
        doRun();
    }

    private void doRun() throws InterruptedException {
        Application.gameManager.createGame("base.planet.corrin", "mountain", 12, 16, 2, new GameManager.GameListener() {

            @Override
            public void onGameCreate(Game game) {
                game.setSpeed(4);
                _testBase.injectModules(game);

                if (createRunnable != null) {
                    createRunnable.run();
                }
            }

            @Override
            public void onGameUpdate(Game game) {
                if (tickRunnable.containsKey(game.getTick())) {
                    tickRunnable.get(game.getTick()).run();
                }
            }

        });

        while (Application.gameManager == null || Application.gameManager.getGame() == null || Application.gameManager.getGame().getTick() < endTick) {
            Thread.sleep(10);
        }

        System.out.println("end");
    }

    public GameTestHelper setUpdateInterval(int updateInterval) {

        Application.APPLICATION_CONFIG.game.updateInterval = Math.max(1, updateInterval);

        return this;
    }
}
