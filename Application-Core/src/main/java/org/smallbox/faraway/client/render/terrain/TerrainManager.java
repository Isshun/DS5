package org.smallbox.faraway.client.render.terrain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import org.smallbox.faraway.client.AssetManager;
import org.smallbox.faraway.client.render.terrain.TerrainLoaderParameters.Type;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.util.Constant;

import static org.smallbox.faraway.util.Constant.HALF_TILE_SIZE;

@ApplicationObject
public class TerrainManager {
    public static final int DARK_COLOR = 0x000000ff;
    public static final String TERRAIN_FULL = "terrain-rock-full";
    public static final String TERRAIN_TOP = "terrain-rock-top";
    public static final String TERRAIN_BOTTOM = "terrain-rock-bottom";
    public static final String TERRAIN_LEFT = "terrain-rock-left";
    public static final String TERRAIN_RIGHT = "terrain-rock-right";
    public static final String TERRAIN_CORNER_TOP_LEFT = "terrain-rock-corner-top-left";
    public static final String TERRAIN_CORNER_TOP_RIGHT = "terrain-rock-corner-top-right";
    public static final String TERRAIN_CORNER_BOTTOM_LEFT = "terrain-rock-corner-bottom-left";
    public static final String TERRAIN_CORNER_BOTTOM_RIGHT = "terrain-rock-corner-bottom-right";
    public static final String TERRAIN_INNER_TOP_LEFT = "terrain-rock-inner-top-left";
    public static final String TERRAIN_INNER_TOP_RIGHT = "terrain-rock-inner-top-right";
    public static final String TERRAIN_INNER_BOTTOM_LEFT = "terrain-rock-inner-bottom-left";
    public static final String TERRAIN_INNER_BOTTOM_RIGHT = "terrain-rock-inner-bottom-right";

    @Inject private AssetManager assetManager;
    public Pixmap pxRock;
    public Pixmap pxGrass;

    public void init() {
        pxRock = createPixmap("data/graphics/texture/g2.png", Pixmap.Format.RGBA8888);
        pxGrass = createPixmap("data/graphics/texture/g1.png", Pixmap.Format.RGBA8888);

        assetManager.setLoader(Pixmap.class, ".terrain", new TerrainLoader(new InternalFileHandleResolver()));

        for (int p = 0; p < 4; p++) {
            assetManager.load(buildPath(TERRAIN_FULL, 0, p), Pixmap.class, new TerrainLoaderParameters(Type.FULL, 0));

            for (int i = 0; i < 6; i++) {
                assetManager.load(buildPath(TERRAIN_TOP, i, p), Pixmap.class, new TerrainLoaderParameters(Type.BORDER_TOP, i));
                assetManager.load(buildPath(TERRAIN_BOTTOM, i, p), Pixmap.class, new TerrainLoaderParameters(Type.BORDER_BOTTOM, i));
                assetManager.load(buildPath(TERRAIN_LEFT, i, p), Pixmap.class, new TerrainLoaderParameters(Type.BORDER_LEFT, i));
                assetManager.load(buildPath(TERRAIN_RIGHT, i, p), Pixmap.class, new TerrainLoaderParameters(Type.BORDER_RIGHT, i));
            }

            assetManager.load(buildPath(TERRAIN_CORNER_TOP_LEFT, 0, p), Pixmap.class, new TerrainLoaderParameters(Type.CORNER_TOP_LEFT, 0));
            assetManager.load(buildPath(TERRAIN_CORNER_TOP_RIGHT, 0, p), Pixmap.class, new TerrainLoaderParameters(Type.CORNER_TOP_RIGHT, 0));
            assetManager.load(buildPath(TERRAIN_CORNER_BOTTOM_LEFT, 0, p), Pixmap.class, new TerrainLoaderParameters(Type.CORNER_BOTTOM_LEFT, 0));
            assetManager.load(buildPath(TERRAIN_CORNER_BOTTOM_RIGHT, 0, p), Pixmap.class, new TerrainLoaderParameters(Type.CORNER_BOTTOM_RIGHT, 0));

            assetManager.load(buildPath(TERRAIN_INNER_TOP_LEFT, 0, p), Pixmap.class, new TerrainLoaderParameters(Type.INNER_TOP_LEFT, 0));
            assetManager.load(buildPath(TERRAIN_INNER_TOP_RIGHT, 0, p), Pixmap.class, new TerrainLoaderParameters(Type.INNER_TOP_RIGHT, 0));
            assetManager.load(buildPath(TERRAIN_INNER_BOTTOM_LEFT, 0, p), Pixmap.class, new TerrainLoaderParameters(Type.INNER_BOTTOM_LEFT, 0));
            assetManager.load(buildPath(TERRAIN_INNER_BOTTOM_RIGHT, 0, p), Pixmap.class, new TerrainLoaderParameters(Type.INNER_BOTTOM_RIGHT, 0));
        }

    }

    public void generate(Pixmap pxRockOut, String key, int position, int outX, int outY) {
        Pixmap mask = assetManager.get(buildPath(key, 0, position));

        for (int x = 0; x < HALF_TILE_SIZE; x++) {
            for (int y = 0; y < HALF_TILE_SIZE; y++) {
                int regularColor = pxRock.getPixel((x + outX) % 512, (y + outY) % 512) & 0xffffff00;
                int alpha = mask.getPixel(x, y) & 0x000000ff;
                int dark = (mask.getPixel(x, y) & 0x0000ff00) >> 8;
                int newColor = applyDarkPattern(regularColor, Math.max(0, dark - 128));
                pxRockOut.drawPixel(outX + x, Constant.TILE_SIZE - 1 - (outY + y), newColor + alpha);
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

    private Pixmap createPixmap(String internalPath, Pixmap.Format format) {
        Texture texture = new Texture(Gdx.files.internal(internalPath), format, false);
        texture.getTextureData().prepare();
        Pixmap pixmap = texture.getTextureData().consumePixmap();
        texture.dispose();
        return pixmap;
    }

}
