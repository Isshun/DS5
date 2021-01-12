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

import static com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888;
import static org.smallbox.faraway.util.Constant.HALF_TILE_SIZE;

public class TerrainLoader extends SynchronousAssetLoader<Pixmap, TerrainLoaderParameters> {

    public TerrainLoader (FileHandleResolver resolver) {
        super(resolver);
    }

    @Override
    public Pixmap load(AssetManager assetManager, String fileName, FileHandle file, TerrainLoaderParameters parameter) {
        Pixmap p1 = createPixmap("data/graphics/texture/g2.png", RGBA8888);
        Pixmap p2Dark = createPixmap("data/graphics/texture/g2_dark.png", RGBA8888);

        Pixmap pixmap = new Pixmap(HALF_TILE_SIZE, HALF_TILE_SIZE, Pixmap.Format.RGBA8888);
        Pixmap alphaMask = parameter.getAlphaMasks().get(0);

        for (int x = 0; x < HALF_TILE_SIZE; x++) {
            for (int y = 0; y < HALF_TILE_SIZE; y++) {
                int modX = parameter.getXFunc().apply(x, y);
                int modY = parameter.getYFunc().apply(x, y);
                int alphaValue = (alphaMask.getPixel(modX, modY) & 0x000000ff);
                float darkValue = (parameter.getDarkMask().getPixel(modX % 128, modY) & 0x000000ff) / 255f;
                int darkColor = p2Dark.getPixel(modX % 512, modY % 512);
                int regularColor = p1.getPixel(modX % 512, modY % 512);
                int color = mergeColor(regularColor, darkColor, darkValue);
                pixmap.drawPixel(x, y, color + alphaValue);
            }
        }

        return pixmap;
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
