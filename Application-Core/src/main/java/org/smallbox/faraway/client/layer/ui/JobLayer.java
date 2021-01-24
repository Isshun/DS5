package org.smallbox.faraway.client.layer.ui;

import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.client.asset.SpriteManager;
import org.smallbox.faraway.client.renderer.BaseRenderer;
import org.smallbox.faraway.client.layer.LayerManager;
import org.smallbox.faraway.client.renderer.Viewport;
import org.smallbox.faraway.client.layer.BaseMapLayer;
import org.smallbox.faraway.core.GameLayer;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.game.building.BuildJob;
import org.smallbox.faraway.game.dig.DigJob;
import org.smallbox.faraway.game.job.JobModule;
import org.smallbox.faraway.game.storage.StoreJob;

@GameObject
@GameLayer(level = LayerManager.JOB_LAYER_LEVEL, visible = true)
public class JobLayer extends BaseMapLayer {
    @Inject private SpriteManager spriteManager;
    @Inject private JobModule jobModule;

    public void onDraw(BaseRenderer renderer, Viewport viewport, double animProgress, int frame) {

        jobModule.getAll().forEach(job -> {

            if (job.getTargetParcel() != null) {

                if (job instanceof StoreJob) {
                    if (((StoreJob)job).sourceConsumable.getFreeQuantity() > 0) {
                        renderer.drawSpriteOnMap(spriteManager.getIcon("graphics/jobs/ic_store.png"), ((StoreJob)job).sourceConsumable.getParcel());
                        renderer.drawTextOnMap(job.getTargetParcel(), "store", Color.CHARTREUSE, 10, 0, 0);
                    }
                }

//            if (job instanceof BasicHaulJob) {
//                renderer.drawOnMap(job.getJobParcel(), spriteManager.getIcon("graphics/jobs/ic_haul.png"));
//                renderer.drawTextOnMap(job.getJobParcel(), "hauling", 10, Color.CHARTREUSE, 0, 0);
//            }

                if (job instanceof BuildJob) {
                    renderer.drawSpriteOnMap(spriteManager.getIcon("graphics/jobs/ic_build.png"), job.getTargetParcel());
                    renderer.drawTextOnMap(job.getTargetParcel(), "building", Color.CHARTREUSE, 10, 0, 0);
                }

//            if (job instanceof BasicCraftJob) {
//                renderer.drawOnMap(job.getTargetParcel(), spriteManager.getIcon("graphics/jobs/ic_craft.png"));
//                renderer.drawTextOnMap(job.getJobParcel(), "craft", 10, Color.CHARTREUSE, 0, 0);
//            }

                if (job.getIcon() != null) {
                    if (job instanceof DigJob) {

                        if (job.getTargetParcel().z == viewport.getFloor()) {
                            renderer.drawSpriteOnMap(spriteManager.getIcon("[base]/graphics/jobs/ic_mining.png"), job.getTargetParcel());
                        } else if (job.getTargetParcel().z == viewport.getFloor() - 1) {
                            renderer.drawSpriteOnMap(spriteManager.getIcon("[base]/graphics/jobs/ic_mining_under.png"), job.getTargetParcel());
                        }

                    } else {
                        renderer.drawSpriteOnMap(spriteManager.getIcon(job.getIcon()), job.getTargetParcel());
                    }
                }

                if (job.getColor() != null) {
                    renderer.drawTextOnMap(job.getTargetParcel(), "gather", job.getColor(), 14, 0, 0);
                }

            }

        });

    }

}