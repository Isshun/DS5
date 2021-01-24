package org.smallbox.faraway.core;

import com.badlogic.gdx.Gdx;
import org.smallbox.faraway.client.input.GameClientObserver;
import org.smallbox.faraway.core.game.GameObserver;
import org.smallbox.faraway.core.game.GameObserverPriority;

import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.function.Consumer;

public class Application {
    private final static Queue<GameObserver> _observers = new PriorityBlockingQueue<>(200, (o1, o2) -> {
        GameObserverPriority.Priority p1 = o1.getClass().isAnnotationPresent(GameObserverPriority.class)
                ? o1.getClass().getAnnotation(GameObserverPriority.class).value()
                : GameObserverPriority.Priority.REGULAR;
        GameObserverPriority.Priority p2 = o2.getClass().isAnnotationPresent(GameObserverPriority.class)
                ? o2.getClass().getAnnotation(GameObserverPriority.class).value()
                : GameObserverPriority.Priority.REGULAR;
        return p1.compareTo(p2);
    });

    public static boolean isLoaded = false;

    public static void addTask(Runnable runnable) {
        Gdx.app.postRunnable(runnable);
    }

    public static void setRunning(boolean isRunning) {
        if (!isRunning && Gdx.app != null) {
            Gdx.app.exit();
        }
    }

    public static void addObserver(GameObserver observer) {
        assert observer != null;

        _observers.add(observer);
    }

    public static void notify(Consumer<GameObserver> action) {
        try {
            _observers.forEach(action);
        } catch (Error | RuntimeException e) {
            e.printStackTrace();
        }
    }

    public static void notifyClient(Consumer<GameClientObserver> action) {
        _observers.forEach(observer -> {
            if (observer instanceof GameClientObserver) {
                action.accept((GameClientObserver) observer);
            }
        });
    }

}