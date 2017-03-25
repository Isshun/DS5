package org.smallbox.faraway.client.render.layer;

import org.smallbox.faraway.client.manager.SpriteManager;
import org.smallbox.faraway.client.render.Viewport;
import org.smallbox.faraway.core.GameLayer;
import org.smallbox.faraway.core.dependencyInjector.BindComponent;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.modules.consumable.BasicHaulJob;
import org.smallbox.faraway.modules.consumable.ConsumableModule;
import org.smallbox.faraway.modules.itemFactory.BasicCraftJob;
import org.smallbox.faraway.modules.job.JobModule;
import org.smallbox.faraway.modules.plant.BasicHarvestJob;
import org.smallbox.faraway.modules.storing.BasicStoreJob;

@GameLayer(level = 2, visible = true)
public class JobLayer extends BaseLayer {

    @BindModule
    private ConsumableModule consumableModule;

    @BindModule
    private JobModule jobModule;

    @BindComponent
    private SpriteManager spriteManager;

    private int[][]         _areas;
    private int             _floor;

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

        consumableModule.getConsumables().forEach(consumableItem -> {
//            renderer.drawOnMap(consumableItem.getParcel(), spriteManager.getTexture("data/graphics/jobs/ic_store.png"));
        });

        //TODO
//        if (_areas == null) {
//            _areas = new int[Application.gameManager.getGame().getInfo().worldWidth][Application.gameManager.getGame().getInfo().worldHeight];
//        }
//
//        int offsetX = viewport.getPosX();
//        int offsetY = viewport.getPosY();
//        int floor = WorldHelper.getCurrentFloor();
//        ModuleHelper.getJobModule().getJobs().stream().filter(job -> !job.isFinish()).forEach(job ->
//                job.drawPixel((x, y, z) -> {
//                    if (floor == z)
//                        renderer.drawPixel(job.getIconDrawable(), offsetX + x * Constant.TILE_WIDTH, offsetY + y * Constant.TILE_HEIGHT);
//                }));
    }


    @Override
    public void onFloorChange(int floor) {
        _floor = floor;
    }
}