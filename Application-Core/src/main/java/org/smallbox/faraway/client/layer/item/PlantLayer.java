package org.smallbox.faraway.client.layer.item;

import com.badlogic.gdx.graphics.g2d.Sprite;
import org.smallbox.faraway.client.asset.SpriteManager;
import org.smallbox.faraway.client.layer.BaseMapLayer;
import org.smallbox.faraway.client.layer.GameLayer;
import org.smallbox.faraway.client.layer.LayerManager;
import org.smallbox.faraway.client.layer.SpriteExtra;
import org.smallbox.faraway.client.renderer.BaseRenderer;
import org.smallbox.faraway.client.renderer.Viewport;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.world.model.MapObjectModel;
import org.smallbox.faraway.game.plant.PlantModule;
import org.smallbox.faraway.game.plant.model.PlantItem;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@GameObject
@GameLayer(level = LayerManager.PLANT_LAYER_LEVEL, visible = true)
public class PlantLayer extends BaseMapLayer {
    @Inject private SpriteManager spriteManager;
    @Inject private PlantModule plantModule;

    private final Map<PlantItem, SpriteExtra> extraMap = new ConcurrentHashMap<>();

    public void onDraw(BaseRenderer renderer, Viewport viewport, double animProgress, int frame) {
        plantModule.getAll().stream()
                .filter(item -> viewport.hasParcel(item.getParcel()))
                .sorted(MapObjectModel.VERTICAL_COMPARATOR)
                .forEach(plant -> drawPlant(renderer, plant));
    }

    private void drawPlant(BaseRenderer renderer, PlantItem plant) {
        if (!extraMap.containsKey(plant)) {
            extraMap.put(plant, spriteManager.buildSpriteExtraParameters(plant.getGraphic()));
        }

        SpriteExtra extra = extraMap.get(plant);
        Sprite sprite = spriteManager.getOrCreateSprite(plant.getGraphic(), hasCursorOver(plant.getParcel()), extra);
        sprite.setAlpha((float) plant.getMaturity());
        sprite.setScale((float) plant.getMaturity());

        renderer.drawSpriteOnMap(sprite, plant.getParcel(), extra.offsetX, extra.offsetY, plant.getInfo().plant.grid, plant.getGridPosition());
    }

}
