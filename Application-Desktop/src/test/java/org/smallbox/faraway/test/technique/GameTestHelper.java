//package org.smallbox.faraway.test.technique;
//
//import org.smallbox.faraway.core.Application;
//import org.smallbox.faraway.core.dependencyInjector.DependencyInjector;
//import org.smallbox.faraway.core.game.Game;
//import org.smallbox.faraway.core.save.GameInfo;
//import org.smallbox.faraway.core.game.GameManager;
//
//import java.util.ArrayList;
//import java.util.Collection;
//
//public class GameTestHelper {
//
//    private final TestBase _testBase;
//
//    public GameTestHelper(TestBase testBase) {
//        _testBase = testBase;
//    }
//
//    public static class RunnableAtTick {
//        public Runnable runnable;
//        public long tick;
//    }
//
//    private Collection<RunnableAtTick> tickRunnable = new ArrayList<>();
//    private GameTestCreateCallback createCallback;
//    private long endTick;
//
//    public interface GameTestCreateCallback {
//        void onGameTestCreate();
//    }
//
//    public static GameTestHelper create(TestBase testBase) {
//        return new GameTestHelper(testBase);
//    }
//
//    public GameTestHelper runOnGameCreate(GameTestCreateCallback createCallback) {
//        this.createCallback = createCallback;
//        return this;
//    }
//
//    public GameTestHelper runOnGameTick(long tick, Runnable runnable) {
//        RunnableAtTick runnableAtTick = new RunnableAtTick();
//        runnableAtTick.tick = tick;
//        runnableAtTick.runnable = runnable;
//        tickRunnable.add(runnableAtTick);
//
//        if (endTick < tick) {
//            endTick = tick;
//        }
//
//        return this;
//    }
//
//    public void runUntil(int tick) throws InterruptedException {
//        endTick = tick;
//        doRun();
//    }
//
//    public void run() throws InterruptedException {
//        doRun();
//    }
//
//    private void doRun() throws InterruptedException {
//        GameManager gameManager = DependencyInjector.getInstance().getDependency(GameManager.class);
//
//        gameManager.createGame(GameInfo.create("base.planet.corrin", "mountain", 12, 16, 2), new GameManager.GameListener() {
//
//            @Override
//            public void onGameCreate(Game game) {
//                game.setSpeed(4);
//                _testBase.injectModules(game);
//
//                if (createCallback != null) {
//                    createCallback.onGameTestCreate();
//                }
//            }
//
//            @Override
//            public void onGameUpdate(Game game) {
//                tickRunnable.stream()
//                        .filter(runnableAtTick -> runnableAtTick.tick == game.getTick())
//                        .forEach(runnableAtTick -> runnableAtTick.runnable.run());
//            }
//
//        });
//
//        while (gameManager == null || gameManager.getGame() == null || Application.gameManager.getGame().getTick() < endTick) {
//            Thread.sleep(10);
//        }
//
//        System.out.println("end");
//    }
//
//}
