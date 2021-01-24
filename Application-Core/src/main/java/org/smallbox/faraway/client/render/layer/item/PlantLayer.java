package org.smallbox.faraway.client.render.layer.item;

import org.smallbox.faraway.client.manager.SpriteManager;
import org.smallbox.faraway.client.render.GDXRendererBase;
import org.smallbox.faraway.client.render.LayerManager;
import org.smallbox.faraway.client.render.Viewport;
import org.smallbox.faraway.client.render.layer.BaseMapLayer;
import org.smallbox.faraway.core.GameLayer;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.modules.plant.PlantModule;
import org.smallbox.faraway.modules.plant.model.PlantItem;

@GameObject
@GameLayer(level = LayerManager.PLANT_LAYER_LEVEL, visible = true)
public class PlantLayer extends BaseMapLayer {
    @Inject private PlantModule plantModule;
    @Inject private SpriteManager spriteManager;

    public void onDraw(GDXRendererBase renderer, Viewport viewport, double animProgress, int frame) {
        plantModule.getAll().stream()
                .filter(item -> viewport.hasParcel(item.getParcel()))
                .forEach(plant -> drawPlant(renderer, plant));

//        tags.removeIf(draw -> draw.frameLeft < 0);
//        tags.forEach(draw -> draw.onTagDraw(layer, viewport));
    }

    private void drawPlant(GDXRendererBase renderer, PlantItem plant) {
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
