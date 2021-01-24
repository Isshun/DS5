package org.smallbox.faraway.client.asset.terrain;

import com.badlogic.gdx.graphics.Pixmap;
import org.smallbox.faraway.client.asset.AssetManager;
import org.smallbox.faraway.client.asset.terrain.TerrainMaskLoaderParameters.Type;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.game.world.Parcel;

import static org.smallbox.faraway.util.Constant.HALF_TILE_SIZE;
import static org.smallbox.faraway.util.Constant.TILE_SIZE;

@ApplicationObject
public class TerrainManager {
    public static final String TERRAIN_FULL = "full";
    public static final String TERRAIN_TOP = "top";
    public static final String TERRAIN_BOTTOM = "bottom";
    public static final String TERRAIN_LEFT = "left";
    public static final String TERRAIN_RIGHT = "right";
    public static final String TERRAIN_CORNER_TOP_LEFT = "corner-top-left";
    public static final String TERRAIN_CORNER_TOP_RIGHT = "corner-top-right";
    public static final String TERRAIN_CORNER_BOTTOM_LEFT = "corner-bottom-left";
    public static final String TERRAIN_CORNER_BOTTOM_RIGHT = "corner-bottom-right";
    public static final String TERRAIN_INNER_TOP_LEFT = "inner-top-left";
    public static final String TERRAIN_INNER_TOP_RIGHT = "inner-top-right";
    public static final String TERRAIN_INNER_BOTTOM_LEFT = "inner-bottom-left";
    public static final String TERRAIN_INNER_BOTTOM_RIGHT = "inner-bottom-right";

    @Inject private AssetManager assetManager;
    @Inject private TerrainMaskLoader terrainMaskLoader;

    public void init() {
        assetManager.setLoader(Pixmap.class, ".terrain", terrainMaskLoader);

        for (int p = 0; p < 4; p++) {
            assetManager.load(buildPath(TERRAIN_FULL, 0, p), Pixmap.class, new TerrainMaskLoaderParameters(Type.FULL, 0));

            for (int i = 0; i < 6; i++) {
                assetManager.load(buildPath(TERRAIN_TOP, i, p), Pixmap.class, new TerrainMaskLoaderParameters(Type.BORDER_TOP, i));
                assetManager.load(buildPath(TERRAIN_BOTTOM, i, p), Pixmap.class, new TerrainMaskLoaderParameters(Type.BORDER_BOTTOM, i));
                assetManager.load(buildPath(TERRAIN_LEFT, i, p), Pixmap.class, new TerrainMaskLoaderParameters(Type.BORDER_LEFT, i));
                assetManager.load(buildPath(TERRAIN_RIGHT, i, p), Pixmap.class, new TerrainMaskLoaderParameters(Type.BORDER_RIGHT, i));
            }

            assetManager.load(buildPath(TERRAIN_CORNER_TOP_LEFT, 0, p), Pixmap.class, new TerrainMaskLoaderParameters(Type.CORNER_TOP_LEFT, 0));
            assetManager.load(buildPath(TERRAIN_CORNER_TOP_RIGHT, 0, p), Pixmap.class, new TerrainMaskLoaderParameters(Type.CORNER_TOP_RIGHT, 0));
            assetManager.load(buildPath(TERRAIN_CORNER_BOTTOM_LEFT, 0, p), Pixmap.class, new TerrainMaskLoaderParameters(Type.CORNER_BOTTOM_LEFT, 0));
            assetManager.load(buildPath(TERRAIN_CORNER_BOTTOM_RIGHT, 0, p), Pixmap.class, new TerrainMaskLoaderParameters(Type.CORNER_BOTTOM_RIGHT, 0));

            assetManager.load(buildPath(TERRAIN_INNER_TOP_LEFT, 0, p), Pixmap.class, new TerrainMaskLoaderParameters(Type.INNER_TOP_LEFT, 0));
            assetManager.load(buildPath(TERRAIN_INNER_TOP_RIGHT, 0, p), Pixmap.class, new TerrainMaskLoaderParameters(Type.INNER_TOP_RIGHT, 0));
            assetManager.load(buildPath(TERRAIN_INNER_BOTTOM_LEFT, 0, p), Pixmap.class, new TerrainMaskLoaderParameters(Type.INNER_BOTTOM_LEFT, 0));
            assetManager.load(buildPath(TERRAIN_INNER_BOTTOM_RIGHT, 0, p), Pixmap.class, new TerrainMaskLoaderParameters(Type.INNER_BOTTOM_RIGHT, 0));
        }

    }

    public void generate(Pixmap pixmapOut, Pixmap pixmapIn, String key, int position, int positionX, int positionY, Parcel parcel) {
        Pixmap mask = assetManager.get(buildPath(key, 0, position));

        for (int x = 0; x < HALF_TILE_SIZE; x++) {
            for (int y = 0; y < HALF_TILE_SIZE; y++) {
                int regularColor = pixmapIn.getPixel((parcel.x * TILE_SIZE % pixmapIn.getWidth()) + positionX + x, (parcel.y * TILE_SIZE % pixmapIn.getHeight()) + positionY + y) & 0xffffff00;
                int alpha = mask.getPixel(x, y) & 0x000000ff;
                int dark = (mask.getPixel(x, y) & 0x0000ff00) >> 8;
                int newColor = applyDarkPattern(regularColor, Math.max(0, dark - 128));
                pixmapOut.drawPixel(positionX + x, TILE_SIZE - 1 - (positionY + y), newColor + alpha);
            }
        }
    }

    public void generate(Pixmap pixmapOut, Pixmap pixmapIn, int positionX, int positionY, Parcel parcel) {
        for (int x = 0; x < TILE_SIZE; x++) {
            for (int y = 0; y < TILE_SIZE; y++) {
                int regularColor = pixmapIn.getPixel((parcel.x * TILE_SIZE % pixmapIn.getWidth()) + positionX + x, (parcel.y * TILE_SIZE % pixmapIn.getHeight()) + positionY + y);
                pixmapOut.drawPixel(positionX + x, TILE_SIZE - 1 - (positionY + y), regularColor);
            }
        }
    }

    private String buildPath(String key, int index, int position) {
        return key + "-" + index + "#" + position + ".terrain";
    }

    private int applyDarkPattern(int regularColor, int ratio) {
        return ((((regularColor >> 24) & 0x000000ff) * (255 - ratio) / 255) << 24) +
                ((((regularColor >> 16) & 0x000000ff) * (255 - ratio) / 255) << 16) +
                ((((regularColor >> 8) & 0x000000ff) * (255 - ratio) / 255) << 8);
    }

}
