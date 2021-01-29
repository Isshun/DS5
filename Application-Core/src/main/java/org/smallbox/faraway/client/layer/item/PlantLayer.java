package org.smallbox.faraway.client.layer.item;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Interpolation;
import org.smallbox.faraway.client.asset.SpriteManager;
import org.smallbox.faraway.client.layer.*;
import org.smallbox.faraway.client.renderer.BaseRenderer;
import org.smallbox.faraway.client.renderer.Viewport;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.game.plant.PlantModule;
import org.smallbox.faraway.game.plant.model.PlantItem;
import org.smallbox.faraway.util.Constant;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@GameObject
@GameLayer(level = LayerManager.PLANT_LAYER_LEVEL, visible = true)
public class PlantLayer extends BaseMapLayer {
    private static final Color COLOR_BASE = new Color(0x00882255);

    @Inject private PlantModule plantModule;
    @Inject private SpriteManager spriteManager;

    private Map<PlantItem, SpriteExtra> extraMap = new ConcurrentHashMap<>();
    private Map<PlantItem, Sprite> spriteMap = new ConcurrentHashMap<>();
    private Map<PlantItem, Sprite> spriteMap2 = new ConcurrentHashMap<>();

    public void onDraw(BaseRenderer renderer, Viewport viewport, double animProgress, int frame) {
        List<PlantItem> plants = plantModule.getAll().stream()
                .filter(item -> viewport.hasParcel(item.getParcel()))
                .sorted((o1, o2) -> (o2.getParcel().y * 2 + o2.getGridPosition() > 1 ? 1 : 0) - (o1.getParcel().y * 2 + o1.getGridPosition() > 1 ? 1 : 0))
                .collect(Collectors.toList());

        plants.forEach(plant -> drawPlant(renderer, plant));

//        tags.removeIf(draw -> draw.frameLeft < 0);
//        tags.forEach(draw -> draw.onTagDraw(layer, viewport));
    }

    private void drawPlant(BaseRenderer renderer, PlantItem plant) {
//        renderer.drawRectangleOnMap(plant.getParcel(), Constant.TILE_SIZE, Constant.TILE_SIZE, COLOR_BASE, 0, 0);

        if (!extraMap.containsKey(plant)) {
            SpriteExtra extra = new SpriteExtra();

            extra.animator = new Animator(-1.5f, 1.5f, 0.005f, Interpolation.pow2, (sprite, value) -> {
                sprite.setOrigin(sprite.getWidth() / 2, sprite.getHeight() - 25);
                sprite.setRotation(value);
            });
            extra.offsetX = new Random().nextInt(10) - 5;
            extra.offsetY = new Random().nextInt(10) - 5;
            extra.flip = new Random().nextBoolean();

            extraMap.put(plant, extra);
        }

        if (!spriteMap.containsKey(plant)) {
            Sprite sprite = new Sprite(spriteManager.getTexture(plant.getGraphic()), 0, 0, plant.getGraphic().width, plant.getGraphic().height);
            sprite.setFlip(false, true);
            spriteMap.put(plant, sprite);
        }

        if (!spriteMap2.containsKey(plant)) {
            Sprite sprite2 = new Sprite(spriteManager.getTexture("data/graphics/plants/wheat_128_2.png"), 0, 0, plant.getGraphic().width, plant.getGraphic().height);
            sprite2.setFlip(false, true);
            spriteMap2.put(plant, sprite2);
        }

        SpriteExtra extra = extraMap.get(plant);

        if (plant.getInfo().name.equals("base.plant.wheat") || plant.getInfo().name.equals("base.plant.corn")) {
            Sprite sprite = extra.animator.update(spriteMap.get(plant));

            if (plant.getInfo().name.equals("base.plant.wheat")) {
                Sprite sprite2 = extra.animator.update(spriteMap2.get(plant));
                drawWheat(plant, renderer, sprite2, extra);
            }

            sprite.setAlpha((float) plant.getMaturity());
            drawWheat(plant, renderer, sprite, extra);
        } else {
            renderer.drawSpriteOnMap(spriteMap.get(plant), plant.getParcel(),
                    Constant.HALF_TILE_SIZE - plant.getGraphic().width / 2,
                    Constant.TILE_SIZE - plant.getGraphic().height);
        }

    }

    private void drawWheat(PlantItem plant, BaseRenderer renderer, Sprite sprite, SpriteExtra extra) {
        sprite.setFlip(extra.flip, true);
        sprite.setScale((float) plant.getMaturity());

        if (plant.getInfo().plant.grid == 4) {

            if (plant.getGridPosition() == 2) {
                renderer.drawSpriteOnMap(sprite, plant.getParcel(),
                        Constant.HALF_TILE_SIZE - plant.getGraphic().width / 2 - 32 + extra.offsetX,
                        Constant.TILE_SIZE - plant.getGraphic().height + extra.offsetY);
            }

            if (plant.getGridPosition() == 3) {
                renderer.drawSpriteOnMap(sprite, plant.getParcel(),
                        Constant.HALF_TILE_SIZE - plant.getGraphic().width / 2 + 32 + extra.offsetX,
                        Constant.TILE_SIZE - plant.getGraphic().height + extra.offsetY);
            }

            if (plant.getGridPosition() == 0) {
                renderer.drawSpriteOnMap(sprite, plant.getParcel(),
                        Constant.HALF_TILE_SIZE - plant.getGraphic().width / 2 - 32 + extra.offsetX,
                        Constant.TILE_SIZE - plant.getGraphic().height - 64 + extra.offsetY);
            }

            if (plant.getGridPosition() == 1) {
                renderer.drawSpriteOnMap(sprite, plant.getParcel(),
                        Constant.HALF_TILE_SIZE - plant.getGraphic().width / 2 + 32 + extra.offsetX,
                        Constant.TILE_SIZE - plant.getGraphic().height - 64 + extra.offsetY);
            }

        } else if (plant.getInfo().plant.grid == 2) {

            if (plant.getGridPosition() == 1) {
                renderer.drawSpriteOnMap(sprite, plant.getParcel(),
                        Constant.HALF_TILE_SIZE - plant.getGraphic().width / 2 - 32 + extra.offsetX,
                        Constant.TILE_SIZE - plant.getGraphic().height + extra.offsetY);
            }

            if (plant.getGridPosition() == 0) {
                renderer.drawSpriteOnMap(sprite, plant.getParcel(),
                        Constant.HALF_TILE_SIZE - plant.getGraphic().width / 2 + 32 + extra.offsetX,
                        Constant.TILE_SIZE - plant.getGraphic().height - 64 + extra.offsetY);
            }

        } else {
            renderer.drawSpriteOnMap(sprite, plant.getParcel(),
                    Constant.HALF_TILE_SIZE - plant.getGraphic().width / 2 + extra.offsetX,
                    Constant.TILE_SIZE - plant.getGraphic().height + extra.offsetY);
        }

    }

    private int getTileForMaturity(PlantItem plant) {
        if (plant.getMaturity() < 0.01) {
            return 0;
        }
        if (plant.getMaturity() < 0.30) {
            return 1;
        }
        if (plant.getMaturity() < 0.60) {
            return 2;
        }
        if (plant.getMaturity() < 0.99) {
            return 3;
        }
        return 4;
    }

//    private Sprite getItemSprite(ConsumableItem consumable) {
////        return ApplicationClient.spriteManager.getSprite(consumable.getInfo(), consumable.getGraphic(), consumable.getInfo().height, 0, 255, false);
//    }
}
