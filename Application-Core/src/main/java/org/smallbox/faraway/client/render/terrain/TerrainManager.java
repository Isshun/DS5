package org.smallbox.faraway.client.render.terrain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import org.smallbox.faraway.client.AssetManager;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.module.world.model.ParcelModel;

import java.util.Arrays;
import java.util.List;

import static com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888;
import static org.smallbox.faraway.util.Constant.HALF_TILE_SIZE;

@ApplicationObject
public class TerrainManager {
    public static final String TERRAIN_FULL = "terrain-rock-full";
    public static final String TERRAIN_TOP = "terrain-rock-top";
    public static final String TERRAIN_BOTTOM = "terrain-rock-bottom";
    public static final String TERRAIN_LEFT = "terrain-rock-left";
    public static final String TERRAIN_RIGHT = "terrain-rock-right";
    public static final String TERRAIN_TOP_LEFT = "terrain-rock-top-left";
    public static final String TERRAIN_TOP_RIGHT = "terrain-rock-top-right";
    public static final String TERRAIN_BOTTOM_LEFT = "terrain-rock-bottom-left";
    public static final String TERRAIN_BOTTOM_RIGHT = "terrain-rock-bottom-right";
    public static final String TERRAIN_INNER_TOP_LEFT = "terrain-rock-inner-top-left";
    public static final String TERRAIN_INNER_TOP_RIGHT = "terrain-rock-inner-top-right";
    public static final String TERRAIN_INNER_BOTTOM_LEFT = "terrain-rock-inner-bottom-left";
    public static final String TERRAIN_INNER_BOTTOM_RIGHT = "terrain-rock-inner-bottom-right";

    @Inject
    private AssetManager assetManager;

    public void init() {
        FileHandleResolver resolver = new InternalFileHandleResolver();
        assetManager.setLoader(Pixmap.class, new TerrainLoader(resolver));

        List<Pixmap> alphaMasks = Arrays.asList(
                createPixmap("data/graphics/texture/mask/blend_mask_border_0.png", RGBA8888)
        );
        List<Pixmap> alphaCornerMasks = Arrays.asList(
                createPixmap("data/graphics/texture/mask/blend_mask_corner_0.png", RGBA8888)
        );
        List<Pixmap> alphaInnerMasks = Arrays.asList(
                createPixmap("data/graphics/texture/mask/blend_mask_inner_0.png", RGBA8888)
        );
        List<Pixmap> alphaFullMasks = Arrays.asList(
                createPixmap("data/graphics/texture/mask/blend_mask_full_0.png", RGBA8888)
        );

        Pixmap darkMask = createPixmap("data/graphics/texture/mask/gradient_mask_border_0.png", RGBA8888);
        Pixmap darkCornerMask = createPixmap("data/graphics/texture/mask/gradient_mask_corner_0.png", RGBA8888);
        Pixmap darkInnerMask = createPixmap("data/graphics/texture/mask/gradient_mask_inner_0.png", RGBA8888);
        Pixmap darkFullMask = createPixmap("data/graphics/texture/mask/gradient_mask_full_0.png", RGBA8888);

        assetManager.load(TERRAIN_FULL + "-" + 0, Pixmap.class, new TerrainLoaderParameters(alphaFullMasks, darkFullMask, (x, y) -> x, (x, y) -> y));

        for (int i = 0; i < alphaMasks.size(); i++) {
            assetManager.load(TERRAIN_TOP + "-" + i, Pixmap.class, new TerrainLoaderParameters(alphaMasks, darkMask, (x, y) -> x, (x, y) -> y));
            assetManager.load(TERRAIN_BOTTOM + "-" + i, Pixmap.class, new TerrainLoaderParameters(alphaMasks, darkMask, (x, y) -> x, (x, y) -> HALF_TILE_SIZE - 1 - y));
            assetManager.load(TERRAIN_LEFT + "-" + i, Pixmap.class, new TerrainLoaderParameters(alphaMasks, darkMask, (x, y) -> y, (x, y) -> x));
            assetManager.load(TERRAIN_RIGHT + "-" + i, Pixmap.class, new TerrainLoaderParameters(alphaMasks, darkMask, (x, y) -> y, (x, y) -> HALF_TILE_SIZE - 1 - x));
        }

        for (int i = 0; i < alphaCornerMasks.size(); i++) {
            assetManager.load(TERRAIN_TOP_LEFT + "-" + i, Pixmap.class, new TerrainLoaderParameters(alphaCornerMasks, darkCornerMask, (x, y) -> x, (x, y) -> y));
            assetManager.load(TERRAIN_TOP_RIGHT + "-" + i, Pixmap.class, new TerrainLoaderParameters(alphaCornerMasks, darkCornerMask, (x, y) -> HALF_TILE_SIZE - 1 - x, (x, y) -> y));
            assetManager.load(TERRAIN_BOTTOM_LEFT + "-" + i, Pixmap.class, new TerrainLoaderParameters(alphaCornerMasks, darkCornerMask, (x, y) -> x, (x, y) -> HALF_TILE_SIZE - 1 - y));
            assetManager.load(TERRAIN_BOTTOM_RIGHT + "-" + i, Pixmap.class, new TerrainLoaderParameters(alphaCornerMasks, darkCornerMask, (x, y) -> HALF_TILE_SIZE - 1 - x, (x, y) -> HALF_TILE_SIZE - 1 - y));
        }

        for (int i = 0; i < alphaInnerMasks.size(); i++) {
            assetManager.load(TERRAIN_INNER_TOP_LEFT + "-" + i, Pixmap.class, new TerrainLoaderParameters(alphaInnerMasks, darkInnerMask, (x, y) -> x, (x, y) -> y));
            assetManager.load(TERRAIN_INNER_TOP_RIGHT + "-" + i, Pixmap.class, new TerrainLoaderParameters(alphaInnerMasks, darkInnerMask, (x, y) -> HALF_TILE_SIZE - 1 - x, (x, y) -> y));
            assetManager.load(TERRAIN_INNER_BOTTOM_LEFT + "-" + i, Pixmap.class, new TerrainLoaderParameters(alphaInnerMasks, darkInnerMask, (x, y) -> x, (x, y) -> HALF_TILE_SIZE - 1 - y));
            assetManager.load(TERRAIN_INNER_BOTTOM_RIGHT + "-" + i, Pixmap.class, new TerrainLoaderParameters(alphaInnerMasks, darkInnerMask, (x, y) -> HALF_TILE_SIZE - 1 - x, (x, y) -> HALF_TILE_SIZE - 1 - y));
        }
    }

    public Pixmap getPixmap(ParcelModel parcel, String key, int position) {
        return assetManager.get(key + "-0");
    }

    private Pixmap createPixmap(String internalPath, Pixmap.Format format) {
        Texture texture = new Texture(Gdx.files.internal(internalPath), format, false);
        texture.getTextureData().prepare();
        return texture.getTextureData().consumePixmap();
    }

}
