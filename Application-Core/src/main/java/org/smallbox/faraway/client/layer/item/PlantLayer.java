package org.smallbox.faraway.client.layer.item;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import org.smallbox.faraway.client.asset.SpriteManager;
import org.smallbox.faraway.client.layer.Animator;
import org.smallbox.faraway.client.layer.BaseMapLayer;
import org.smallbox.faraway.client.layer.GameLayer;
import org.smallbox.faraway.client.layer.LayerManager;
import org.smallbox.faraway.client.renderer.BaseRenderer;
import org.smallbox.faraway.client.renderer.Viewport;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.game.plant.PlantModule;
import org.smallbox.faraway.game.plant.model.PlantItem;
import org.smallbox.faraway.util.Constant;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@GameObject
@GameLayer(level = LayerManager.PLANT_LAYER_LEVEL, visible = true)
public class PlantLayer extends BaseMapLayer {
    @Inject private PlantModule plantModule;
    @Inject private SpriteManager spriteManager;

    private Map<PlantItem, Animator> animatorMap = new ConcurrentHashMap<>();
    private Map<PlantItem, Sprite> spriteMap = new ConcurrentHashMap<>();

    public void onDraw(BaseRenderer renderer, Viewport viewport, double animProgress, int frame) {
        plantModule.getAll().stream()
                .filter(item -> viewport.hasParcel(item.getParcel()))
                .forEach(plant -> drawPlant(renderer, plant));

//        tags.removeIf(draw -> draw.frameLeft < 0);
//        tags.forEach(draw -> draw.onTagDraw(layer, viewport));
    }

    private void drawPlant(BaseRenderer renderer, PlantItem plant) {
        renderer.drawRectangleOnMap(plant.getParcel(), Constant.TILE_SIZE, Constant.TILE_SIZE, new Color(0x00882255), 0, 0);

        if (!animatorMap.containsKey(plant)) {
            animatorMap.put(plant, new Animator(-1.5f, 1.5f, 0.005f, (sprite, value) -> {
                sprite.setOrigin(sprite.getWidth() / 2, sprite.getHeight() - 25);
                sprite.setRotation(value);
            }));
        }

        if (!spriteMap.containsKey(plant)) {
            Sprite sprite = new Sprite(spriteManager.getTexture(plant.getGraphic()), 0, 0, plant.getGraphic().width, plant.getGraphic().height);
            sprite.setFlip(false, true);
            spriteMap.put(plant, sprite);
        }

        Sprite sprite = spriteMap.get(plant);

        animatorMap.get(plant).update(sprite);

        renderer.drawSpriteOnMap(sprite, plant.getParcel(),
                Constant.HALF_TILE_SIZE - plant.getGraphic().width / 2,
                Constant.TILE_SIZE - plant.getGraphic().height);
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
