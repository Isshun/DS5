package org.smallbox.faraway.client.module;

import org.smallbox.faraway.common.ClientGameTask;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by 300206 on 14/09/2017.
 */
@GameObject
public class TaskClientModule {

    public Map<Long, ClientGameTask> tasks = new ConcurrentHashMap<>();

    public Collection<ClientGameTask> getTasks() {
        return tasks.values();
    }

    public void update(ClientGameTask task) {
        tasks.put(task.id, task);
    }

    public void remove(long taskId) {
        tasks.remove(taskId);
    }
}
