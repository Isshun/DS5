package org.smallbox.faraway.core.task;

import com.badlogic.gdx.Gdx;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.util.Log;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Alex on 19/11/2016.
 */
public class TaskManager {
    private LoaderThreadExecutor _loadExecutor = new LoaderThreadExecutor();
    private List<LoadTask>                      _loadTasks = new CopyOnWriteArrayList<>();
    private boolean                             _running = true;

    public TaskManager() {
        launchBackgroundThread(() -> {
            for (LoadTask task: _loadTasks) {
                if (task.state == LoadTask.State.RUNNING) {
                    return;
                }
                if (task.state == LoadTask.State.NONE) {
                    startTask(task);
                    return;
                }
            }
        }, 10);
    }

    private void startTask(LoadTask task) {
        if (task.onMainThread) {
            _loadExecutor.submit(() -> Gdx.app.postRunnable(() -> runLoadTask(task)));
        } else {
            _loadExecutor.submit(() -> runLoadTask(task));
        }
    }

    public void addLoadTask(String label, boolean onMainThread, Runnable runnable) {
        _loadTasks.add(new LoadTask(label, onMainThread) {
            @Override
            protected void onRun() {
                runnable.run();
            }
        });
    }

    private void runLoadTask(LoadTask task) {
        if (_running) {
            Log.info("Run load task:" + task.label);
            task.run();
            if (task.throwable != null) {
                System.out.println("Run load task:" + task.label + " has throw an exception");
                System.out.println(task.throwable.getMessage());
                task.throwable.printStackTrace();
                exitWithError(task.throwable);
            }
        }
    }

    private void exitWithError(Throwable throwable) {
        _running = false;
        _loadExecutor.shutdown();
        Application.exitWithError();
    }

    public List<LoadTask> getLoadTasks() {
        return _loadTasks;
    }

    public void launchBackgroundThread(Runnable runnable, int timeInterval) {
        Log.info("Launch background thread");
        new Thread(() -> {
            while (_running) {
                try {
                    runnable.run();
                    Thread.sleep(timeInterval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Log.info("Background thread terminated");
        }).start();
    }

    public void launchBackgroundThread(Runnable runnable) {
        Log.info("Launch background thread");
        new Thread(runnable::run).start();
    }
}
