package org.smallbox.faraway.client.layer.item;

import org.smallbox.faraway.client.asset.SpriteManager;
import org.smallbox.faraway.client.renderer.BaseRenderer;
import org.smallbox.faraway.client.layer.LayerManager;
import org.smallbox.faraway.client.renderer.Viewport;
import org.smallbox.faraway.client.layer.BaseMapLayer;
import org.smallbox.faraway.client.layer.GameLayer;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.game.plant.PlantModule;
import org.smallbox.faraway.game.plant.model.PlantItem;

@GameObject
@GameLayer(level = LayerManager.PLANT_LAYER_LEVEL, visible = true)
public class PlantLayer extends BaseMapLayer {
    @Inject private PlantModule plantModule;
    @Inject private SpriteManager spriteManager;

    public void onDraw(BaseRenderer renderer, Viewport viewport, double animProgress, int frame) {
        plantModule.getAll().stream()
                .filter(item -> viewport.hasParcel(item.getParcel()))
                .forEach(plant -> drawPlant(renderer, plant));

//        tags.removeIf(draw -> draw.frameLeft < 0);
//        tags.forEach(draw -> draw.onTagDraw(layer, viewport));
    }

    private void drawPlant(BaseRenderer renderer, PlantItem plant) {
        renderer.drawSpriteOnMap(spriteManager.getNewSprite(plant.getGraphic(), getTileForMaturity(plant)), plant.getParcel()
        );
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
