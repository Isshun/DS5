package org.smallbox.faraway.client.render.terrain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;

import java.util.*;

import static com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888;
import static org.smallbox.faraway.util.Constant.HALF_TILE_SIZE;

@ApplicationObject
public class TerrainManager {
    private final int[] RANDS = {4, 8, 1, 5, 0, 7, 6, 1, 4, 8, 9, 5, 7, 6, 7, 1, 0, 3, 2, 7, 4, 5, 6, 8, 3, 9, 1, 4, 3, 0, 2, 7, 9, 6, 5, 7, 2, 3, 6, 4, 9};
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

    private Map<String, TerrainLoaderParameters> parameters;
    private Pixmap p2Dark;
    private Pixmap p2Sand;
    private Pixmap maskTransition;

    public void init() {
        p2Dark = createPixmap("data/graphics/texture/g2_dark.png", RGBA8888);
        p2Sand = createPixmap("data/graphics/texture/sand.png", RGBA8888);
        maskTransition = createPixmap("data/graphics/texture/mask/blend_mask_transition_5.png", RGBA8888);

        List<Pixmap> alphaMasks = Arrays.asList(
                createPixmap("data/graphics/texture/mask/blend_mask_border_0.png", RGBA8888),
                createPixmap("data/graphics/texture/mask/blend_mask_border_1.png", RGBA8888),
                createPixmap("data/graphics/texture/mask/blend_mask_border_2.png", RGBA8888),
                createPixmap("data/graphics/texture/mask/blend_mask_border_3.png", RGBA8888),
                createPixmap("data/graphics/texture/mask/blend_mask_border_4.png", RGBA8888),
                createPixmap("data/graphics/texture/mask/blend_mask_border_5.png", RGBA8888)
        );
        List<Pixmap> alphaCornerMasks = Collections.singletonList(
                createPixmap("data/graphics/texture/mask/blend_mask_corner_0.png", RGBA8888)
        );
        List<Pixmap> alphaInnerMasks = Collections.singletonList(
                createPixmap("data/graphics/texture/mask/blend_mask_inner_0.png", RGBA8888)
        );
        List<Pixmap> alphaFullMasks = Collections.singletonList(
                createPixmap("data/graphics/texture/mask/blend_mask_full_0.png", RGBA8888)
        );

        Pixmap darkMask = createPixmap("data/graphics/texture/mask/gradient_mask_border_0.png", RGBA8888);
        Pixmap darkCornerMask = createPixmap("data/graphics/texture/mask/gradient_mask_corner_0.png", RGBA8888);
        Pixmap darkInnerMask = createPixmap("data/graphics/texture/mask/gradient_mask_inner_0.png", RGBA8888);
        Pixmap darkFullMask = createPixmap("data/graphics/texture/mask/gradient_mask_full_0.png", RGBA8888);

        parameters = new HashMap<>();
        parameters.put(TERRAIN_FULL, new TerrainLoaderParameters(alphaFullMasks, darkFullMask, (x, y) -> x, (x, y) -> y));

        for (int i = 0; i < alphaMasks.size(); i++) {
            parameters.put(TERRAIN_TOP, new TerrainLoaderParameters(alphaMasks, darkMask, (x, y) -> x, (x, y) -> y));
            parameters.put(TERRAIN_BOTTOM, new TerrainLoaderParameters(alphaMasks, darkMask, (x, y) -> x, (x, y) -> HALF_TILE_SIZE - 1 - y));
            parameters.put(TERRAIN_LEFT, new TerrainLoaderParameters(alphaMasks, darkMask, (x, y) -> y, (x, y) -> x));
            parameters.put(TERRAIN_RIGHT, new TerrainLoaderParameters(alphaMasks, darkMask, (x, y) -> y, (x, y) -> HALF_TILE_SIZE - 1 - x));
        }

        for (int i = 0; i < alphaCornerMasks.size(); i++) {
            parameters.put(TERRAIN_TOP_LEFT, new TerrainLoaderParameters(alphaCornerMasks, darkCornerMask, (x, y) -> x, (x, y) -> y));
            parameters.put(TERRAIN_TOP_RIGHT, new TerrainLoaderParameters(alphaCornerMasks, darkCornerMask, (x, y) -> HALF_TILE_SIZE - 1 - x, (x, y) -> y));
            parameters.put(TERRAIN_BOTTOM_LEFT, new TerrainLoaderParameters(alphaCornerMasks, darkCornerMask, (x, y) -> x, (x, y) -> HALF_TILE_SIZE - 1 - y));
            parameters.put(TERRAIN_BOTTOM_RIGHT, new TerrainLoaderParameters(alphaCornerMasks, darkCornerMask, (x, y) -> HALF_TILE_SIZE - 1 - x, (x, y) -> HALF_TILE_SIZE - 1 - y));
        }

        for (int i = 0; i < alphaInnerMasks.size(); i++) {
            parameters.put(TERRAIN_INNER_TOP_LEFT, new TerrainLoaderParameters(alphaInnerMasks, darkInnerMask, (x, y) -> x, (x, y) -> y));
            parameters.put(TERRAIN_INNER_TOP_RIGHT, new TerrainLoaderParameters(alphaInnerMasks, darkInnerMask, (x, y) -> HALF_TILE_SIZE - 1 - x, (x, y) -> y));
            parameters.put(TERRAIN_INNER_BOTTOM_LEFT, new TerrainLoaderParameters(alphaInnerMasks, darkInnerMask, (x, y) -> x, (x, y) -> HALF_TILE_SIZE - 1 - y));
            parameters.put(TERRAIN_INNER_BOTTOM_RIGHT, new TerrainLoaderParameters(alphaInnerMasks, darkInnerMask, (x, y) -> HALF_TILE_SIZE - 1 - x, (x, y) -> HALF_TILE_SIZE - 1 - y));
        }

    }

    public void generate(Pixmap pxRock, Pixmap pxRockOut, String key, int position, int outX, int outY) {
        TerrainLoaderParameters parameter = parameters.get(key);
        Pixmap alphaMask = parameter.getAlphaMasks().get(RANDS[(outX + position + outY * 42) % RANDS.length] % parameter.getAlphaMasks().size());

        for (int x = 0; x < HALF_TILE_SIZE; x++) {
            for (int y = 0; y < HALF_TILE_SIZE; y++) {
                int maskX = parameter.getXFunc().apply(x, y);
                int maskY = parameter.getYFunc().apply(x, y);
                int alphaValue = (alphaMask.getPixel(maskX, maskY) & 0x000000ff);
                int darkValue = (parameter.getDarkMask().getPixel(maskX % 128, maskY) & 0x000000ff);
                int darkColor = p2Dark.getPixel((x + outX) % 512, (y + outY) % 512);
//                int transitionValue = (maskTransition.getPixel(maskX % 64, maskY) & 0x000000ff);
//                int transitionColor = p2Sand.getPixel((x + outX) % 512, (y + outY) % 512);
                int regularColor = pxRock.getPixel((x + outX) % 512, (y + outY) % 512);
                int color = mergeColor(regularColor, darkColor, darkValue);
//                int color2 = mergeColor(color, transitionColor, transitionValue);
                pxRockOut.drawPixel(outX + x, outY + y, color + alphaValue);
            }
        }
    }

    private int mergeColor(int regularColor, int darkColor, int ratio) {
        return (((((darkColor >> 24) & 0x000000ff) * ratio / 255) + (((regularColor >> 24) & 0x000000ff) * (255 - ratio) / 255)) << 24) +
                (((((darkColor >> 16) & 0x000000ff) * ratio / 255) + (((regularColor >> 16) & 0x000000ff) * (255 - ratio) / 255)) << 16) +
                (((((darkColor >> 8) & 0x000000ff) * ratio / 255) + (((regularColor >> 8) & 0x000000ff) * (255 - ratio) / 255)) << 8);
    }

    private Pixmap createPixmap(String internalPath, Pixmap.Format format) {
        Texture texture = new Texture(Gdx.files.internal(internalPath), format, false);
        texture.getTextureData().prepare();
        Pixmap pixmap = texture.getTextureData().consumePixmap();
        texture.dispose();
        return pixmap;
    }

}
