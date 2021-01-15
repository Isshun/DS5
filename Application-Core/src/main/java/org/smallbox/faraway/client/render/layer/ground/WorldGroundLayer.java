package org.smallbox.faraway.client.render.layer.ground;

import com.badlogic.gdx.graphics.Texture;
import org.smallbox.faraway.client.AssetManager;
import org.smallbox.faraway.client.render.GDXRenderer;
import org.smallbox.faraway.client.render.LayerManager;
import org.smallbox.faraway.client.render.Viewport;
import org.smallbox.faraway.client.render.layer.BaseLayer;
import org.smallbox.faraway.client.render.layer.ground.chunkGenerator.RockTileGenerator;
import org.smallbox.faraway.core.GameLayer;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.modelInfo.GraphicInfo;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.world.WorldModule;

import java.util.Optional;

import static org.smallbox.faraway.util.Constant.TILE_SIZE;

@GameObject
@GameLayer(level = LayerManager.WORLD_GROUND_LAYER_LEVEL, visible = true)
public class WorldGroundLayer extends BaseLayer {
    @Inject private WorldModule worldModule;
    @Inject private RockTileGenerator rockTileGenerator;
    @Inject private AssetManager assetManager;

    @Override
    public void onDraw(GDXRenderer renderer, Viewport viewport, double animProgress, int frame) {
//        int fromX = Math.max((int) ((-viewport.getPosX() / Constant.TILE_SIZE) * viewport.getScale()), 0);
//        int fromY = Math.max((int) ((-viewport.getPosY() / Constant.TILE_SIZE) * viewport.getScale()), 0);
//
//        // TODO: take right panel in consideration
//        int tileWidthCount = (int) (applicationConfig.getResolutionWidth() / (Constant.TILE_SIZE * viewport.getScale()));
//        int tileHeightCount = (int) (applicationConfig.getResolutionHeight() / (Constant.TILE_SIZE * viewport.getScale()));
//        int toX = Math.min(fromX + tileWidthCount, game.getInfo().worldWidth);
//        int toY = Math.min(fromY + tileHeightCount, game.getInfo().worldHeight);

        int viewportX = viewport.getPosX();
        int viewportY = viewport.getPosY();

        for (int x = 0; x < worldModule.getWidth(); x++) {
            for (int y = 0; y < worldModule.getHeight(); y++) {
                ParcelModel parcel = worldModule.getParcel(x, y, viewport.getFloor());
                Optional.ofNullable(parcel.getGroundInfo().getGraphicInfo(GraphicInfo.Type.TERRAIN)).ifPresent(graphicInfo ->
                        renderer.draw(
                                assetManager.lazyLoad("data" + graphicInfo.path, Texture.class), viewportX + (parcel.x * TILE_SIZE),
                                viewportY + (parcel.y * TILE_SIZE),
                                TILE_SIZE, TILE_SIZE));

                if (parcel.hasRock()) {
                    renderer.draw(rockTileGenerator.getTexture(parcel), viewportX + (x * TILE_SIZE), viewportY + (y * TILE_SIZE));
                }
            }
        }
    }

}