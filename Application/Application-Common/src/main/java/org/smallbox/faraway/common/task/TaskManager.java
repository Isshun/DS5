package org.smallbox.faraway.common.task;

import org.smallbox.faraway.common.GameException;
import org.smallbox.faraway.common.dependencyInjector.ApplicationObject;
import org.smallbox.faraway.common.util.Log;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Alex on 19/11/2016.
 */
@ApplicationObject
public class TaskManager {
    private static int BACKGROUND_THREAD_LIMIT = 2;

    private LoaderThreadExecutor                _loadExecutor = new LoaderThreadExecutor();
    private ExecutorService                     _backgroundExecutor = Executors.newFixedThreadPool(BACKGROUND_THREAD_LIMIT);
    private Collection<LoadTask>                _loadTasks = new LinkedBlockingQueue<>();
    private boolean                             _running = true;
    private int                                 _backgroundThreadCount;
    private RunInterface runInterface;

    public TaskManager() {
        launchBackgroundThread(() -> {
            for (LoadTask task: _loadTasks) {
                if (task.state == LoadTask.State.NONE) {
                    task.state = LoadTask.State.WAITING;
                    startTask(task);
                    return;
                }
            }
        }, 10);
    }

    private void startTask(LoadTask task) {
//        if (task.onMainThread) {
//            _loadExecutor.submit(() -> Application.runOnMainThread(() -> runLoadTask(task)));
//        } else {
//            _loadExecutor.submit(() -> runLoadTask(task));
//        }

        // TODO: no bg thread during early dev
        this.runInterface.onRun(() -> runLoadTask(task));
    }

    public interface RunInterface {
        void onRun(Runnable runnable);
    }

    public void setRunInterface(RunInterface runInterface) {
        this.runInterface = runInterface;
    }

    public void addLoadTask(String label, boolean onMainThread, Task task) {
        _loadTasks.add(new LoadTask(label, true) {
            @Override
            protected void onRun() {
                task.run();
            }
        });
    }

    private void runLoadTask(LoadTask task) {
        if (_running) {
            Log.info("Run load task:" + task.label);
            task.state = LoadTask.State.RUNNING;
            task.run();
            task.state = LoadTask.State.COMPLETE;
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
        _running = false;
        _loadExecutor.shutdown();
        System.exit(1);
    }

    public Collection<LoadTask> getLoadTasks() {
        return _loadTasks;
    }

    public void launchBackgroundThread(Task task, int timeInterval) {
        if (_backgroundThreadCount++ < BACKGROUND_THREAD_LIMIT) {

            Log.info("Launch background thread");

            _backgroundExecutor.submit(() -> {
                while (_running) {
                    task.run();
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

}
