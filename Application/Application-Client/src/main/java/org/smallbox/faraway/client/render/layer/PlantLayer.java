package org.smallbox.faraway.client.render.layer;

import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.client.GameLayer;
import org.smallbox.faraway.client.PlantCommon;
import org.smallbox.faraway.client.module.PlantClientModule;
import org.smallbox.faraway.client.render.LayerManager;
import org.smallbox.faraway.client.render.Viewport;
import org.smallbox.faraway.common.dependencyInjector.BindComponent;
import org.smallbox.faraway.common.dependencyInjector.GameObject;

/**
 * Created by Alex on 31/07/2016.
 */
@GameObject
@GameLayer(level = LayerManager.PLANT_LAYER_LEVEL, visible = true)
public class PlantLayer extends BaseLayer {

    @BindComponent
    private PlantClientModule plantModule;

    @Override
    public void onGameInit() {
    }

    public void onDraw(GDXRenderer renderer, Viewport viewport, double animProgress, int frame) {
        plantModule.getPlants().stream()
                .filter(plant -> viewport.hasParcel(plant.parcelX, plant.parcelY, plant.parcelZ))
                .forEach(plant -> drawPlant(renderer, plant));

//        tags.removeIf(draw -> draw.frameLeft < 0);
//        tags.forEach(draw -> draw.onTagDraw(layer, viewport));
    }

    private void drawPlant(GDXRenderer renderer, PlantCommon plant) {
        renderer.drawRectangleOnMap(plant.parcelX, plant.parcelY, 16, 16, Color.CHARTREUSE, true, 0, 0);
//        renderer.drawOnMap(plant.parcelX, plant.parcelY,
//                ApplicationClient.spriteManager.getNewSprite(plant.getGraphic(), getTileForMaturity(plant)));
    }

//    private int getTileForMaturity(PlantItem plant) {
//        if (plant.getMaturity() < 0.01) { return 0; }
//        if (plant.getMaturity() < 0.30) { return 1; }
//        if (plant.getMaturity() < 0.60) { return 2; }
//        if (plant.getMaturity() < 0.99) { return 3; }
//        return 4;
//    }

//    private Sprite getItemSprite(ConsumableItem consumable) {
////        return ApplicationClient.spriteManager.getSprite(consumable.getInfo(), consumable.getGraphic(), consumable.getInfo().height, 0, 255, false);
//    }
}
