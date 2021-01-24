package org.smallbox.faraway.core.task;

import com.badlogic.gdx.Gdx;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.util.GameException;
import org.smallbox.faraway.util.log.Log;

import java.util.Arrays;
import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Supplier;

@ApplicationObject
public class TaskManager {
    private static final int BACKGROUND_THREAD_LIMIT = 20;

    private final LoaderThreadExecutor loadExecutor = new LoaderThreadExecutor();
    private final ExecutorService backgroundExecutor = Executors.newFixedThreadPool(BACKGROUND_THREAD_LIMIT);
    private final Queue<Task> tasks = new LinkedBlockingQueue<>();
    private boolean running = true;
    private int backgroundThreadCount;

    public TaskManager() {
        launchBackgroundThread(() -> {
            for (Task task : tasks) {
                if (task.state == State.NONE || task.state == State.BLOCKING || (task instanceof WaitTask && task.state == State.WAITING)) {
                    if (task instanceof LoadTask) {
                        task.state = State.WAITING;
                    }
                    startTask(task);
                    return;
                }
            }
        }, 10);
    }

    private void startTask(Task task) {
//        if (task.onMainThread) {
//            _loadExecutor.submit(() -> Application.runOnMainThread(() -> runLoadTask(task)));
//        } else {
//            _loadExecutor.submit(() -> runLoadTask(task));
//        }

        // TODO: no bg thread during early dev
        Gdx.app.postRunnable(() -> runTask(task));
    }

    private void runTask(Task task) {
        if (running) {
            Log.info("Run load task:" + task.getLabel());
            if (task instanceof WaitTask) {
                task.state = State.BLOCKING;
            }
            if (task instanceof LoadTask) {
                task.state = State.RUNNING;
            }
            if (task.state != State.COMPLETE && task.run()) {
                task.state = State.COMPLETE;
            }
            if (task.throwable != null) {
                System.out.println("Run load task:" + task.label + " has throw an exception");
                System.out.println(task.throwable.getMessage());
                Arrays.asList(task.throwable.getStackTrace()).forEach(System.out::println);
                task.throwable.printStackTrace();
                System.out.println("-- END --");
                exitWithError(task.throwable);
            }
        }
    }

    private void exitWithError(Throwable throwable) {
        running = false;
        loadExecutor.shutdown();
        Application.setRunning(false);
    }

    public Collection<Task> getLoadTasks() {
        return tasks;
    }

    public void launchBackgroundThread(Runnable runnable, int timeInterval) {
        if (backgroundThreadCount++ < BACKGROUND_THREAD_LIMIT) {

            Log.info("Launch background thread");

            backgroundExecutor.submit(() -> {
                while (running) {
                    runnable.run();
                    try {
                        Thread.sleep(timeInterval);
                    } catch (InterruptedException e) {
                        exitWithError(e);
                        throw new RuntimeException(e);
                    }
                }

                Log.info("Background thread terminated");
            });

        } else {
            throw new GameException(TaskManager.class, "BACKGROUND_THREAD_LIMIT exceeded");
        }
    }

    public void addLoadTask(String label, boolean onMainThread, Runnable runnable) {
        tasks.add(new LoadTask(label, true) {
            @Override
            protected void onRun() {
                runnable.run();
            }
        });
    }

    public void addWaitTask(String label, boolean onMainThread, Supplier<Boolean> runnable, Supplier<Float> progressSupplier) {
        tasks.add(new WaitTask(label, true, progressSupplier) {
            @Override
            protected boolean onRun() {
                return runnable.get();
            }
        });
    }

}
