package org.smallbox.faraway.client.render.layer;

import org.smallbox.faraway.client.ApplicationClient;
import org.smallbox.faraway.client.render.LayerManager;
import org.smallbox.faraway.client.render.Viewport;
import org.smallbox.faraway.core.GameLayer;
import org.smallbox.faraway.core.dependencyInjector.Inject;
import org.smallbox.faraway.core.dependencyInjector.GameObject;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.modules.plant.PlantModule;
import org.smallbox.faraway.modules.plant.model.PlantItem;

/**
 * Created by Alex on 31/07/2016.
 */
@GameObject
@GameLayer(level = LayerManager.PLANT_LAYER_LEVEL, visible = true)
public class PlantLayer extends BaseLayer {

    @Inject
    private PlantModule plantModule;

    public void onDraw(GDXRenderer renderer, Viewport viewport, double animProgress, int frame) {
        plantModule.getPlants().stream()
                .filter(item -> viewport.hasParcel(item.getParcel()))
                .forEach(plant -> drawPlant(renderer, plant));

//        tags.removeIf(draw -> draw.frameLeft < 0);
//        tags.forEach(draw -> draw.onTagDraw(layer, viewport));
    }

    private void drawPlant(GDXRenderer renderer, PlantItem plant) {
        renderer.drawOnMap(plant.getParcel(),
                ApplicationClient.spriteManager.getNewSprite(plant.getGraphic(), getTileForMaturity(plant)));
    }

    private int getTileForMaturity(PlantItem plant) {
        if (plant.getMaturity() < 0.01) { return 0; }
        if (plant.getMaturity() < 0.30) { return 1; }
        if (plant.getMaturity() < 0.60) { return 2; }
        if (plant.getMaturity() < 0.99) { return 3; }
        return 4;
    }

//    private Sprite getItemSprite(ConsumableItem consumable) {
////        return ApplicationClient.spriteManager.getSprite(consumable.getInfo(), consumable.getGraphic(), consumable.getInfo().height, 0, 255, false);
//    }
}
