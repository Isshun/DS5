package org.smallbox.faraway.client.debug.dashboard.content;

import org.smallbox.faraway.client.debug.dashboard.DashboardLayerBase;
import org.smallbox.faraway.client.module.TaskClientModule;
import org.smallbox.faraway.client.render.layer.GDXRenderer;
import org.smallbox.faraway.core.dependencyInjector.GameObject;
import org.smallbox.faraway.core.dependencyInjector.Inject;

@GameObject
public class TaskDashboardLayer extends DashboardLayerBase {

    @Inject
    private TaskClientModule taskClientModule;

    @Override
    protected void onDraw(GDXRenderer renderer, int frame) {
        if (taskClientModule != null && taskClientModule.getTasks() != null) {
            taskClientModule.getTasks().forEach(task -> drawDebug(renderer, "Task ", task.id + " " + task.label + " " + task.elapsed + " " + task.duration ));
        }
    }

}
