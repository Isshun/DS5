package org.smallbox.faraway.client.render.terrain;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.graphics.Pixmap;

import java.util.List;
import java.util.function.BiFunction;

public class TerrainLoaderParameters extends AssetLoaderParameters<Pixmap> {
    private List<Pixmap> alphaMasks;
    private Pixmap darkMask;
    private BiFunction<Integer, Integer, Integer> xFunc;
    private BiFunction<Integer, Integer, Integer> yFunc;

    public TerrainLoaderParameters(List<Pixmap> alphaMasks, Pixmap darkMask, BiFunction<Integer, Integer, Integer> xFunc, BiFunction<Integer, Integer, Integer> yFunc) {
        this.alphaMasks = alphaMasks;
        this.darkMask = darkMask;
        this.xFunc = xFunc;
        this.yFunc = yFunc;
    }

    public List<Pixmap> getAlphaMasks() {
        return alphaMasks;
    }

    public Pixmap getDarkMask() {
        return darkMask;
    }

    public BiFunction<Integer, Integer, Integer> getXFunc() {
        return xFunc;
    }

    public BiFunction<Integer, Integer, Integer> getYFunc() {
        return yFunc;
    }
}
