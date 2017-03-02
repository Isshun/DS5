package org.smallbox.faraway.client.renderer;

import org.smallbox.faraway.client.ApplicationClient;
import org.smallbox.faraway.core.GameRenderer;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.modules.flora.PlantModule;

/**
 * Created by Alex on 31/07/2016.
 */
@GameRenderer(level = MainRenderer.CONSUMABLE_RENDERER_LEVEL, visible = true)
public class PlantRenderer extends BaseRenderer {

    @BindModule
    private PlantModule plantModule;

    @Override
    public void onGameCreate(Game game) {
    }

    public void onDraw(GDXRenderer renderer, Viewport viewport, double animProgress, int frame) {
        plantModule.getPlants().stream()
                .filter(item -> viewport.hasParcel(item.getParcel()))
                .forEach(plant -> {
                    renderer.drawOnMap(plant.getParcel(), ApplicationClient.spriteManager.getNewSprite(plant.getGraphic()));
//                    renderer.drawTextOnMap(consumable.getParcel().x, consumable.getParcel().y, "x" + consumable.getQuantity(), 12, Color.BLUE, 0, 0);
                });

//        tags.removeIf(draw -> draw.frameLeft < 0);
//        tags.forEach(draw -> draw.onTagDraw(renderer, viewport));
    }

//    private Sprite getItemSprite(ConsumableItem consumable) {
////        return ApplicationClient.spriteManager.getSprite(consumable.getInfo(), consumable.getGraphic(), consumable.getInfo().height, 0, 255, false);
//    }
}
