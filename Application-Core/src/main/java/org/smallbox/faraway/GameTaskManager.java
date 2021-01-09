package org.smallbox.faraway;

import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.game.Game;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@GameObject
public class GameTaskManager {

    private final List<GameTask> gameTasksToRemove = new ArrayList<>();
    private final Queue<GameTask> gameTasks = new ConcurrentLinkedQueue<>();

    public void startTask(GameTask task) {
        System.out.println("Start task: " + task.label);

        gameTasks.add(task);
        task.onStart();

//        Application.gameServer.serialize("START", task.name, task.id, task);
    }

    public void update() {
        gameTasks.forEach(task -> {
            task.elapsed += Game.interval;
            task.update();

            if (task.isComplete()) {
                gameTasksToRemove.add(task);
            }

//            Application.gameServer.serialize("UDPATE", task.name, task.id, task);
        });

        gameTasksToRemove.forEach(this::stopTask);
        gameTasksToRemove.clear();
    }

    public void stopTask(GameTask task) {
        System.out.println("Stop task: " + task.label);

        gameTasks.remove(task);
        task.onClose();

//        Application.gameServer.serialize("STOP", task.name, task.id, task);
    }

}
