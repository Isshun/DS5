package org.smallbox.faraway.client.render.layer.ground;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import org.smallbox.faraway.client.manager.SpriteManager;
import org.smallbox.faraway.client.render.GDXRenderer;
import org.smallbox.faraway.client.render.LayerManager;
import org.smallbox.faraway.client.render.Viewport;
import org.smallbox.faraway.client.render.layer.BaseLayer;
import org.smallbox.faraway.client.render.layer.ground.chunkGenerator.RockTileGenerator;
import org.smallbox.faraway.client.render.terrain.TerrainManager;
import org.smallbox.faraway.core.GameLayer;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.OnGameLayerInit;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.model.MovableModel;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.game.service.applicationConfig.ApplicationConfig;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.world.WorldModule;
import org.smallbox.faraway.modules.world.factory.WorldFactory;
import org.smallbox.faraway.modules.world.factory.WorldFactoryDebug;
import org.smallbox.faraway.util.Constant;
import org.smallbox.faraway.util.log.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@GameObject
@GameLayer(level = LayerManager.WORLD_GROUND_LAYER_LEVEL, visible = true)
public class WorldGroundLayer extends BaseLayer {
    @Inject private WorldModule worldModule;
    @Inject private RockTileGenerator rockTileGenerator;
    @Inject private Viewport viewport;
    @Inject private Game game;
    @Inject private Data data;
    @Inject private SpriteManager spriteManager;
    @Inject private ApplicationConfig applicationConfig;
    @Inject private TerrainManager terrainManager;
    @Inject private WorldFactory worldFactory;
    @Inject private WorldFactoryDebug worldFactoryDebug;

    private final ExecutorService _executor = Executors.newSingleThreadExecutor();
    private boolean[][] _rockLayersUpToDate;
    private int _rows;
    private int _cols;
    private Map<ItemInfo, Pixmap> _pxLiquids;
    private Map<ItemInfo, Pixmap> _pxGroundBorders;
    private Map<ParcelModel, Texture> parcelTextures = new HashMap<>();
    private Texture textureIn;
    private Sprite groundSprite;
    private Sprite groundSprite2;

    @OnGameLayerInit
    public void onGameLayerInit() {
        textureIn = new Texture("data/graphics/texture/grass.png");

        groundSprite = new Sprite(textureIn);
        groundSprite.setSize(Constant.TILE_SIZE, Constant.TILE_SIZE);
        groundSprite2 = new Sprite(new Texture("data/graphics/texture/g2_ground.png"));
        groundSprite2.setSize(Constant.TILE_SIZE, Constant.TILE_SIZE);
    }

    @Override
    public void onRemoveRock(ParcelModel parcel) {
    }

    public void onDraw(GDXRenderer renderer, Viewport viewport, double animProgress, int frame) {
        int fromX = Math.max((int) ((-viewport.getPosX() / Constant.TILE_SIZE) * viewport.getScale()), 0);
        int fromY = Math.max((int) ((-viewport.getPosY() / Constant.TILE_SIZE) * viewport.getScale()), 0);

        // TODO: take right panel in consideration
        int tileWidthCount = (int) (applicationConfig.getResolutionWidth() / (Constant.TILE_SIZE * viewport.getScale()));
        int tileHeightCount = (int) (applicationConfig.getResolutionHeight() / (Constant.TILE_SIZE * viewport.getScale()));
        int toX = Math.min(fromX + tileWidthCount, game.getInfo().worldWidth);
        int toY = Math.min(fromY + tileHeightCount, game.getInfo().worldHeight);

        int viewportX = viewport.getPosX();
        int viewportY = viewport.getPosY();

        for (int x = 0; x < worldModule.getWidth(); x++) {
            for (int y = 0; y < worldModule.getHeight(); y++) {
                ParcelModel parcel = worldModule.getParcel(x, y, viewport.getFloor());

                if (parcel.getGroundInfo().name.equals("base.ground.grass")) {
                    renderer.draw(viewportX + (x * Constant.TILE_SIZE), viewportY + (y * Constant.TILE_SIZE), groundSprite);
                } else {
                    renderer.draw(viewportX + (x * Constant.TILE_SIZE), viewportY + (y * Constant.TILE_SIZE), groundSprite2);
                }

                if (parcel.hasRock()) {
                    if (!parcelTextures.containsKey(parcel)) {
                        int neighborhood = computeNeighborhood(parcel);
                        parcelTextures.put(parcel, rockTileGenerator.getTexture(parcel, neighborhood));
                    }
                    renderer.draw(viewportX + (x * Constant.TILE_SIZE), viewportY + (y * Constant.TILE_SIZE), parcelTextures.get(parcel));
                }
            }
        }

        Sprite sprite = new Sprite(new Texture(worldFactoryDebug.getPixmap()));
        sprite.setPosition(100, 0);
        sprite.setScale(1);
        renderer.drawUI(sprite);
    }

    private int computeNeighborhood(ParcelModel parcel) {
        int neighborhood = 0;
        neighborhood |= worldModule.checkOrNull(parcel, ParcelModel::hasRock, MovableModel.Direction.TOP_LEFT)       ? 0b100000000 : 0b000000000;
        neighborhood |= worldModule.checkOrNull(parcel, ParcelModel::hasRock, MovableModel.Direction.TOP)            ? 0b010000000 : 0b000000000;
        neighborhood |= worldModule.checkOrNull(parcel, ParcelModel::hasRock, MovableModel.Direction.TOP_RIGHT)      ? 0b001000000 : 0b000000000;
        neighborhood |= worldModule.checkOrNull(parcel, ParcelModel::hasRock, MovableModel.Direction.LEFT)           ? 0b000100000 : 0b000000000;
        neighborhood |= worldModule.checkOrNull(parcel, ParcelModel::hasRock, MovableModel.Direction.NONE)           ? 0b000010000 : 0b000000000;
        neighborhood |= worldModule.checkOrNull(parcel, ParcelModel::hasRock, MovableModel.Direction.RIGHT)          ? 0b000001000 : 0b000000000;
        neighborhood |= worldModule.checkOrNull(parcel, ParcelModel::hasRock, MovableModel.Direction.BOTTOM_LEFT)    ? 0b000000100 : 0b000000000;
        neighborhood |= worldModule.checkOrNull(parcel, ParcelModel::hasRock, MovableModel.Direction.BOTTOM)         ? 0b000000010 : 0b000000000;
        neighborhood |= worldModule.checkOrNull(parcel, ParcelModel::hasRock, MovableModel.Direction.BOTTOM_RIGHT)   ? 0b000000001 : 0b000000000;
        return neighborhood;
    }

    @Override
    public void onFloorChange(int floor) {
//        _rockLayersUpToDate = new boolean[_cols][_rows];
        long startTime = System.currentTimeMillis();
        for (int col = 0; col < _cols; col++) {
            for (int row = 0; row < _rows; row++) {
                long time = System.currentTimeMillis();
//                worldGroundChunkGenerator.createGround(col, row);
//                worldRockChunkGenerator.createGround(col, row, floor);
//                Log.info("generate tile " + col + "x" + row + " in " + ((System.currentTimeMillis() - time)) + "ms");
            }
        }
        Log.info("generate all tiles in " + ((System.currentTimeMillis() - startTime)) + "ms");
    }
}