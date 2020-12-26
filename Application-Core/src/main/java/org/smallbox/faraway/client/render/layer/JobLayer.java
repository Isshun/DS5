package org.smallbox.faraway.client.render.layer;

import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.client.manager.SpriteManager;
import org.smallbox.faraway.client.render.LayerManager;
import org.smallbox.faraway.client.render.Viewport;
import org.smallbox.faraway.core.GameLayer;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.modules.building.BasicBuildJob;
import org.smallbox.faraway.modules.consumable.BasicHaulJob;
import org.smallbox.faraway.modules.itemFactory.BasicCraftJob;
import org.smallbox.faraway.modules.job.JobModule;
import org.smallbox.faraway.modules.storage.BasicStoreJob;

@GameObject
@GameLayer(level = LayerManager.JOB_LAYER_LEVEL, visible = true)
public class JobLayer extends BaseLayer {

    @Inject
    private JobModule jobModule;

    @Inject
    private SpriteManager spriteManager;

    public void onDraw(GDXRenderer renderer, Viewport viewport, double animProgress, int frame) {

        jobModule.getJobs().forEach(job -> {

            if (job instanceof BasicStoreJob) {
                ((BasicStoreJob)job).getConsumables().forEach(consumable -> {
                    renderer.drawOnMap(consumable.getParcel(), spriteManager.getIcon("graphics/jobs/ic_store.png"));
                    renderer.drawTextOnMap(job.getJobParcel(), "store", 10, Color.CHARTREUSE, 0, 0);
                });
            }

            if (job instanceof BasicHaulJob) {
                renderer.drawOnMap(job.getJobParcel(), spriteManager.getIcon("graphics/jobs/ic_haul.png"));
                renderer.drawTextOnMap(job.getJobParcel(), "hauling", 10, Color.CHARTREUSE, 0, 0);
            }

            if (job instanceof BasicBuildJob) {
                renderer.drawOnMap(job.getJobParcel(), spriteManager.getIcon("graphics/jobs/ic_build.png"));
                renderer.drawTextOnMap(job.getJobParcel(), "building", 10, Color.CHARTREUSE, 0, 0);
            }

            if (job instanceof BasicCraftJob) {
                renderer.drawOnMap(job.getTargetParcel(), spriteManager.getIcon("graphics/jobs/ic_craft.png"));
                renderer.drawTextOnMap(job.getJobParcel(), "craft", 10, Color.CHARTREUSE, 0, 0);
            }

            if (job.getIcon() != null) {
                renderer.drawOnMap(job.getTargetParcel(), spriteManager.getIcon(job.getIcon()));
            }

            if (job.getColor() != null) {
                renderer.drawTextOnMap(job.getJobParcel(), "gather", 10, job.getColor(), 0, 0);
            }

        });

    }

}