package org.smallbox.faraway.client.renderer;

import org.smallbox.faraway.client.ApplicationClient;
import org.smallbox.faraway.core.GameRenderer;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.modules.flora.PlantModule;
import org.smallbox.faraway.modules.flora.model.PlantItem;

/**
 * Created by Alex on 31/07/2016.
 */
@GameRenderer(level = MainRenderer.CONSUMABLE_RENDERER_LEVEL, visible = true)
public class PlantRenderer extends BaseRenderer {

    @BindModule
    private PlantModule plantModule;

    @Override
    public void onGameCreateObserver(Game game) {
    }

    public void onDraw(GDXRenderer renderer, Viewport viewport, double animProgress, int frame) {
        plantModule.getPlants().stream()
                .filter(item -> viewport.hasParcel(item.getParcel()))
                .forEach(plant -> drawPlant(renderer, plant));

//        tags.removeIf(draw -> draw.frameLeft < 0);
//        tags.forEach(draw -> draw.onTagDraw(renderer, viewport));
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
