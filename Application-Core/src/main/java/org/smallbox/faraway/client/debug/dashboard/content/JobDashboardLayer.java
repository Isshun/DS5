package org.smallbox.faraway.client.debug.dashboard.content;

import org.smallbox.faraway.client.debug.dashboard.DashboardLayerBase;
import org.smallbox.faraway.client.renderer.BaseRenderer;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.game.job.JobModule;

@GameObject
public class JobDashboardLayer extends DashboardLayerBase {
    @Inject private JobModule jobModule;

    @Override
    protected void onDraw(BaseRenderer renderer, int frame) {
        jobModule.getAll().forEach(job -> {
            drawDebug(renderer, "JOB",
                    String.format("%s, %.2f",
                            job.getMainLabel(),
                            job.getProgress()
                    )
            );
            job.getTasks().forEach(task -> drawDebug(renderer, "JOB", "  - " + task.label));
        });
    }

}
