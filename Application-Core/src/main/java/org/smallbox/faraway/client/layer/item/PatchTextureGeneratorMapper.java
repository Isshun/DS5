package org.smallbox.faraway.client.layer.item;

import java.util.function.BiFunction;

class PatchTextureGeneratorMapper {
    private final BiFunction<Integer, Integer, Integer> xFunc;
    private final BiFunction<Integer, Integer, Integer> yFunc;

    public PatchTextureGeneratorMapper(BiFunction<Integer, Integer, Integer> xFunc, BiFunction<Integer, Integer, Integer> yFunc) {
        this.xFunc = xFunc;
        this.yFunc = yFunc;
    }

    public int getX(int x, int y) {
        return xFunc.apply(x, y);
    }

    public int getY(int x, int y) {
        return yFunc.apply(x, y);
    }
}
