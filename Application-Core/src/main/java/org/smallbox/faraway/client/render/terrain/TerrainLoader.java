package org.smallbox.faraway.client.render.terrain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.SynchronousAssetLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888;
import static org.smallbox.faraway.util.Constant.HALF_TILE_SIZE;

public class TerrainLoader extends SynchronousAssetLoader<Pixmap, TerrainLoaderParameters> {
    private final int[] RANDS = {4, 8, 1, 5, 0, 7, 6, 1, 4, 8, 9, 5, 7, 6, 7, 1, 0, 3, 2, 7, 4, 5, 6, 8, 3, 9, 1, 4, 3, 0, 2, 7, 9, 6, 5, 7, 2, 3, 6, 4, 9};

    private List<Pixmap> alphaBorderMasks;
    private List<Pixmap> alphaCornerMasks;
    private List<Pixmap> alphaInnerMasks;
    private List<Pixmap> alphaFullMasks;
    private Pixmap darkBorderMask;
    private Pixmap darkCornerMask;
    private Pixmap darkInnerMask;
    private Pixmap darkFullMask;

    public TerrainLoader (FileHandleResolver resolver) {
        super(resolver);

        alphaBorderMasks = Arrays.asList(
                createPixmap("data/graphics/texture/mask/blend_mask_border_0.png", RGBA8888),
                createPixmap("data/graphics/texture/mask/blend_mask_border_1.png", RGBA8888),
                createPixmap("data/graphics/texture/mask/blend_mask_border_2.png", RGBA8888),
                createPixmap("data/graphics/texture/mask/blend_mask_border_3.png", RGBA8888),
                createPixmap("data/graphics/texture/mask/blend_mask_border_4.png", RGBA8888),
                createPixmap("data/graphics/texture/mask/blend_mask_border_5.png", RGBA8888)
        );

        alphaCornerMasks = Collections.singletonList(
                createPixmap("data/graphics/texture/mask/blend_mask_corner_0.png", RGBA8888)
        );

        alphaInnerMasks = Collections.singletonList(
                createPixmap("data/graphics/texture/mask/blend_mask_inner_0.png", RGBA8888)
        );

        alphaFullMasks = Collections.singletonList(
                createPixmap("data/graphics/texture/mask/blend_mask_full_0.png", RGBA8888)
        );

        darkBorderMask = createPixmap("data/graphics/texture/mask/gradient_mask_border_0.png", RGBA8888);
        darkCornerMask = createPixmap("data/graphics/texture/mask/gradient_mask_corner_0.png", RGBA8888);
        darkInnerMask = createPixmap("data/graphics/texture/mask/gradient_mask_inner_0.png", RGBA8888);
        darkFullMask = createPixmap("data/graphics/texture/mask/gradient_mask_full_0.png", RGBA8888);
    }

    @Override
    public Pixmap load(AssetManager assetManager, String fileName, FileHandle file, TerrainLoaderParameters parameter) {
        Pixmap alphaMask = getRandMask(getAlphaMask(parameter));
        Pixmap darkMask = getDarkMask(parameter);
        Pixmap pixmap = new Pixmap(HALF_TILE_SIZE, HALF_TILE_SIZE, RGBA8888);

        for (int x = 0; x < HALF_TILE_SIZE; x++) {
            for (int y = 0; y < HALF_TILE_SIZE; y++) {
                int maskX = getX(parameter, x, y);
                int maskY = getY(parameter, x, y);
                int alphaValue = (alphaMask.getPixel(maskX, maskY) & 0x000000ff);
                int darkValue = (darkMask.getPixel(maskX, maskY) & 0x000000ff) << 8;
//                int darkColor = p2Dark.getPixel((x + outX) % 512, (y + outY) % 512);
//                int regularColor = pxRock.getPixel((x + outX) % 512, (y + outY) % 512);
//                int color = mergeColor(regularColor, darkColor, darkValue);
                pixmap.drawPixel(x, y, alphaValue + darkValue);
//                pxRockOut.drawPixel(outX + x, outY + y, color + alphaValue);
            }
        }

        return pixmap;
    }

    private Pixmap getRandMask(List<Pixmap> masks) {
//        masks.get(RANDS[(outX + position + outY * 42) % RANDS.length] % parameter.getAlphaMasks().size())
        return masks.get(0);
    }

    private List<Pixmap> getAlphaMask(TerrainLoaderParameters parameters) {
        if (parameters.isBorder()) {
            return alphaBorderMasks;
        }
        if (parameters.isCorner()) {
            return alphaCornerMasks;
        }
        if (parameters.isInner()) {
            return alphaInnerMasks;
        }
        return alphaFullMasks;
    }

    private Pixmap getDarkMask(TerrainLoaderParameters parameters) {
        if (parameters.isBorder()) {
            return darkBorderMask;
        }
        if (parameters.isCorner()) {
            return darkCornerMask;
        }
        if (parameters.isInner()) {
            return darkInnerMask;
        }
        return darkFullMask;
    }

//    TERRAIN_TOP, (x, y) -> x, (x, y) -> y));
//    TERRAIN_BOTTOM, (x, y) -> x, (x, y) -> HALF_TILE_SIZE - 1 - y));
//    TERRAIN_LEFT, (x, y) -> y, (x, y) -> x));
//    TERRAIN_RIGHT, (x, y) -> y, (x, y) -> HALF_TILE_SIZE - 1 - x));
//    TERRAIN_CORNER_TOP_LEFT, (x, y) -> x, (x, y) -> y));
//    TERRAIN_CORNER_TOP_RIGHT, (x, y) -> HALF_TILE_SIZE - 1 - x, (x, y) -> y));
//    TERRAIN_CORNER_BOTTOM_LEFT, (x, y) -> x, (x, y) -> HALF_TILE_SIZE - 1 - y));
//    TERRAIN_CORNER_BOTTOM_RIGHT, (x, y) -> HALF_TILE_SIZE - 1 - x, (x, y) -> HALF_TILE_SIZE - 1 - y));
//    TERRAIN_INNER_TOP_LEFT, (x, y) -> x, (x, y) -> y));
//    TERRAIN_INNER_TOP_RIGHT, (x, y) -> HALF_TILE_SIZE - 1 - x, (x, y) -> y));
//    TERRAIN_INNER_BOTTOM_LEFT, (x, y) -> x, (x, y) -> HALF_TILE_SIZE - 1 - y));
//    TERRAIN_INNER_BOTTOM_RIGHT, (x, y) -> HALF_TILE_SIZE - 1 - x, (x, y) -> HALF_TILE_SIZE - 1 - y));

    private int getX(TerrainLoaderParameters parameter, int x, int y) {
        switch (parameter.getType()) {
            default:
                return x;
            case BORDER_LEFT:
            case BORDER_RIGHT:
                return y;
            case CORNER_TOP_RIGHT:
            case CORNER_BOTTOM_RIGHT:
            case INNER_TOP_RIGHT:
            case INNER_BOTTOM_RIGHT:
                return HALF_TILE_SIZE - 1 - x;
        }
    }

    private int getY(TerrainLoaderParameters parameter, int x, int y) {
        switch (parameter.getType()) {
            default:
                return y;
            case BORDER_LEFT:
                return x;
            case BORDER_RIGHT:
                return HALF_TILE_SIZE - 1 - x;
            case BORDER_BOTTOM:
            case CORNER_BOTTOM_LEFT:
            case CORNER_BOTTOM_RIGHT:
            case INNER_BOTTOM_LEFT:
            case INNER_BOTTOM_RIGHT:
                return HALF_TILE_SIZE - 1 - y;
        }
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, TerrainLoaderParameters parameter) {
        return null;
    }

    private Pixmap createPixmap(String internalPath, Pixmap.Format format) {
        Texture texture = new Texture(Gdx.files.internal(internalPath), format, false);
        texture.getTextureData().prepare();
        return texture.getTextureData().consumePixmap();
    }

    private int mergeColor(int regularColor, int darkColor, float ratio) {
        return (((int) ((((darkColor >> 24) & 0x000000ff) * ratio) + (((regularColor >> 24) & 0x000000ff) * (1 - ratio)))) << 24) +
                (((int) ((((darkColor >> 16) & 0x000000ff) * ratio) + (((regularColor >> 16) & 0x000000ff) * (1 - ratio)))) << 16) +
                (((int) ((((darkColor >> 8) & 0x000000ff) * ratio) + (((regularColor >> 8) & 0x000000ff) * (1 - ratio)))) << 8);
    }

}
