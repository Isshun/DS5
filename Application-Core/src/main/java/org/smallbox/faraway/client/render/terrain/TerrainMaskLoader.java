package org.smallbox.faraway.client.render.terrain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.Array;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.OnInit;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888;
import static org.smallbox.faraway.util.Constant.HALF_TILE_SIZE;

@ApplicationObject
public class TerrainMaskLoader extends SynchronousAssetLoader<Pixmap, TerrainMaskLoaderParameters> {
    private final int[] RANDS = {4, 8, 1, 5, 0, 7, 6, 1, 4, 8, 9, 5, 7, 6, 7, 1, 0, 3, 2, 7, 4, 5, 6, 8, 3, 9, 1, 4, 3, 0, 2, 7, 9, 6, 5, 7, 2, 3, 6, 4, 9};

    private List<Pixmap> alphaBorderMasks;
    private List<Pixmap> alphaCornerMasks;
    private List<Pixmap> alphaInnerMasks;
    private List<Pixmap> alphaFullMasks;
    private Pixmap darkBorderMask;
    private Pixmap darkCornerMask;
    private Pixmap darkInnerMask;
    private Pixmap darkFullMask;

    public TerrainMaskLoader() {
        super(new InternalFileHandleResolver());
    }

    @OnInit
    private void init() {
        alphaBorderMasks = Arrays.asList(
                createPixmap("data/graphics/texture/mask/blend_mask_border_0.png"),
                createPixmap("data/graphics/texture/mask/blend_mask_border_1.png"),
                createPixmap("data/graphics/texture/mask/blend_mask_border_2.png"),
                createPixmap("data/graphics/texture/mask/blend_mask_border_3.png"),
                createPixmap("data/graphics/texture/mask/blend_mask_border_4.png"),
                createPixmap("data/graphics/texture/mask/blend_mask_border_5.png")
        );

        alphaCornerMasks = Collections.singletonList(
                createPixmap("data/graphics/texture/mask/blend_mask_corner_0.png")
        );

        alphaInnerMasks = Collections.singletonList(
                createPixmap("data/graphics/texture/mask/blend_mask_inner_0.png")
        );

        alphaFullMasks = Collections.singletonList(
                createPixmap("data/graphics/texture/mask/blend_mask_full_0.png")
        );

        darkBorderMask = createPixmap("data/graphics/texture/mask/gradient_mask_border_0.png");
        darkCornerMask = createPixmap("data/graphics/texture/mask/gradient_mask_corner_0.png");
        darkInnerMask = createPixmap("data/graphics/texture/mask/gradient_mask_inner_0.png");
        darkFullMask = createPixmap("data/graphics/texture/mask/gradient_mask_full_0.png");
    }

    @Override
    public Pixmap load(AssetManager assetManager, String fileName, FileHandle file, TerrainMaskLoaderParameters parameter) {
        Pixmap alphaMask = getRandMask(getAlphaMask(parameter));
        Pixmap darkMask = getDarkMask(parameter);
        Pixmap pixmap = new Pixmap(HALF_TILE_SIZE, HALF_TILE_SIZE, RGBA8888);

        for (int x = 0; x < HALF_TILE_SIZE; x++) {
            for (int y = 0; y < HALF_TILE_SIZE; y++) {
                int maskX = getX(parameter, x, y);
                int maskY = getY(parameter, x, y);
                int alphaValue = (alphaMask.getPixel(maskX, maskY) & 0x000000ff);
                int darkValue = (darkMask.getPixel(maskX, maskY) & 0x000000ff) << 8;
                pixmap.drawPixel(x, y, alphaValue + darkValue);
            }
        }

        return pixmap;
    }

    private Pixmap getRandMask(List<Pixmap> masks) {
//        masks.get(RANDS[(outX + position + outY * 42) % RANDS.length] % parameter.getAlphaMasks().size())
        return masks.get(0);
    }

    private List<Pixmap> getAlphaMask(TerrainMaskLoaderParameters parameters) {
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

    private Pixmap getDarkMask(TerrainMaskLoaderParameters parameters) {
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

    private int getX(TerrainMaskLoaderParameters parameter, int x, int y) {
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

    private int getY(TerrainMaskLoaderParameters parameter, int x, int y) {
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
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, TerrainMaskLoaderParameters parameter) {
        return null;
    }

    private Pixmap createPixmap(String internalPath) {
        return new Pixmap(Gdx.files.internal(internalPath));
    }

}
