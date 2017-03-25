package org.smallbox.faraway.client.render.layer;

import org.smallbox.faraway.client.manager.SpriteManager;
import org.smallbox.faraway.client.render.LayerManager;
import org.smallbox.faraway.client.render.Viewport;
import org.smallbox.faraway.core.GameLayer;
import org.smallbox.faraway.core.dependencyInjector.BindComponent;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.modules.consumable.BasicHaulJob;
import org.smallbox.faraway.modules.itemFactory.BasicCraftJob;
import org.smallbox.faraway.modules.job.JobModule;
import org.smallbox.faraway.modules.plant.BasicHarvestJob;
import org.smallbox.faraway.modules.storing.BasicStoreJob;

@GameLayer(level = LayerManager.JOB_LAYER_LEVEL, visible = true)
public class JobLayer extends BaseLayer {

    @BindModule
    private JobModule jobModule;

    @BindComponent
    private SpriteManager spriteManager;

    public void onDraw(GDXRenderer renderer, Viewport viewport, double animProgress, int frame) {

        jobModule.getJobs().forEach(job -> {

            if (job instanceof BasicStoreJob) {
                ((BasicStoreJob)job).getConsumables().keySet().forEach(consumable ->
                        renderer.drawOnMap(consumable.getParcel(), spriteManager.getIcon("graphics/jobs/ic_store.png")));
            }

            if (job instanceof BasicHaulJob) {
                ((BasicHaulJob)job).getConsumables().keySet().forEach(consumable ->
                        renderer.drawOnMap(consumable.getParcel(), spriteManager.getIcon("graphics/jobs/ic_haul.png")));
            }

            if (job instanceof BasicCraftJob) {
                renderer.drawOnMap(job.getTargetParcel(), spriteManager.getIcon("graphics/jobs/ic_craft.png"));
            }

            if (job instanceof BasicHarvestJob) {
                renderer.drawOnMap(job.getTargetParcel(), spriteManager.getIcon("graphics/jobs/ic_gather.png"));
            }

        });

    }

}