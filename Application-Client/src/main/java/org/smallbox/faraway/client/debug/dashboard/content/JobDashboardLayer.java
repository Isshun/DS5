package org.smallbox.faraway.client.debug.dashboard.content;

import org.smallbox.faraway.client.debug.dashboard.DashboardLayerBase;
import org.smallbox.faraway.client.render.layer.GDXRenderer;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.modules.job.JobModule;

@GameObject
public class JobDashboardLayer extends DashboardLayerBase {

    @Inject
    private JobModule jobModule;

    @Override
    protected void onDraw(GDXRenderer renderer, int frame) {
        if (jobModule != null && jobModule.getJobs() != null) {
            jobModule.getJobs().forEach(job -> {
                drawDebug(renderer, "JOB",
                        String.format("%s, %.2f, %s",
                                job.getMainLabel(),
                                job.getProgress(),
                                job.getLastReturn()
                        )
                );
                job.getTasks().forEach(task -> drawDebug(renderer, "JOB", "  - " + task.label));
            });
        }
    }

}
