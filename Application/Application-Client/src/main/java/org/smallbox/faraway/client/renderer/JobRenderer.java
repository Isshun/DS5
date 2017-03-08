package org.smallbox.faraway.client.renderer;

import org.smallbox.faraway.client.manager.SpriteManager;
import org.smallbox.faraway.core.GameRenderer;
import org.smallbox.faraway.core.dependencyInjector.BindComponent;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.modules.consumable.BasicHaulJobToFactory;
import org.smallbox.faraway.modules.consumable.BasicStoreJob;
import org.smallbox.faraway.modules.consumable.ConsumableModule;
import org.smallbox.faraway.modules.flora.BasicHarvestJob;
import org.smallbox.faraway.modules.item.factory.BasicCraftJob;
import org.smallbox.faraway.modules.job.JobModule;

@GameRenderer(level = 2, visible = true)
public class JobRenderer extends BaseRenderer {

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

            if (job instanceof BasicHaulJobToFactory) {
                ((BasicHaulJobToFactory)job).getConsumables().keySet().forEach(consumable ->
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