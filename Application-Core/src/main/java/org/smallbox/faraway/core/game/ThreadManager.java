package org.smallbox.faraway.core.game;

import org.smallbox.faraway.core.dependencyInjector.DependencyManager;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.OnGameLongUpdate;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.OnGameUpdate;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.OnInit;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

@ApplicationObject
public class ThreadManager {
    private final static int GAME_UPDATE_DELAY = 40;
    private final static int GAME_LONG_UPDATE_DELAY = 1000;

    @Inject private DependencyManager dependencyManager;
    @Inject private MonitoringManager monitoringManager;
    @Inject private GameTime gameTime;
    @Inject private Game game;

    private final ScheduledExecutorService moduleScheduler = Executors.newSingleThreadScheduledExecutor();
//    private final ExecutorService threadPoolExecutor = Executors.newWorkStealingPool();
    private final ExecutorService threadPoolExecutor = Executors.newFixedThreadPool(1);
    private final Map<Object, Thread> threadMap = new ConcurrentHashMap<>();
    private final Map<Object, Queue<Runnable>> runnableMap = new ConcurrentHashMap<>();

    public long         getTickInterval() { return 1; }
    public double       getHourInterval() { return 1 / game.getTickPerHour(); }

    @OnInit
    public void onInit() {
        moduleScheduler.scheduleAtFixedRate(() -> callGameMethod(OnGameUpdate.class), 0, GAME_UPDATE_DELAY, MILLISECONDS);
        moduleScheduler.scheduleAtFixedRate(() -> callGameMethod(OnGameLongUpdate.class), 0, GAME_LONG_UPDATE_DELAY, MILLISECONDS);
    }

    private void callGameMethod(Class<? extends Annotation> cls) {
        if (game != null && game.getStatus() == GameStatus.STARTED) {
            dependencyManager.callMethodAnnotatedBy(cls);
        }
    }

    public void addRunnable(Object model, Runnable runnable) {
        threadPoolExecutor.submit(() -> monitoringManager.encapsulate(model, runnable).run());
    }
//
//    public void addRunnable2(Object model, Runnable runnable) {
//        threadPoolExecutor.submit(() -> {
//            monitoringManager.encapsulate(model, runnable).run();
//            if (model instanceof AbsGameModule) {
//                ((AbsGameModule) model).updateGame();
//            }
//        });
//
//        if (!threadMap.containsKey(model)) {
//            Queue<Runnable> runnables = new ConcurrentLinkedQueue<>();
//            runnableMap.put(model, runnables);
//            Thread thread = new Thread(() -> {
//                while (true) {
//
//                    try {
//                        Thread.sleep(10);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//            thread.setName("Game / " + model.getClass().getSimpleName());
//            thread.start();
//            threadMap.put(model, thread);
//        }
//        runnableMap.get(model).add(runnable);
//    }

}
