package org.smallbox.faraway.client.render.terrain;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.graphics.Pixmap;

import static org.smallbox.faraway.client.render.terrain.TerrainMaskLoaderParameters.Type.*;
import static org.smallbox.faraway.client.render.terrain.TerrainMaskLoaderParameters.Type.INNER_BOTTOM_RIGHT;

public class TerrainMaskLoaderParameters extends AssetLoaderParameters<Pixmap> {

    public enum Type {
        FULL,
        BORDER_TOP,
        BORDER_BOTTOM,
        BORDER_LEFT,
        BORDER_RIGHT,
        CORNER_TOP_LEFT,
        CORNER_TOP_RIGHT,
        CORNER_BOTTOM_LEFT,
        CORNER_BOTTOM_RIGHT,
        INNER_TOP_LEFT,
        INNER_TOP_RIGHT,
        INNER_BOTTOM_LEFT,
        INNER_BOTTOM_RIGHT
    }

    private final int index;
    private final Type type;

    public TerrainMaskLoaderParameters(Type type, int index) {
        this.type = type;
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public Type getType() {
        return type;
    }

    public boolean isBorder() {
        return type == BORDER_TOP || type == BORDER_BOTTOM || type == BORDER_LEFT || type == BORDER_RIGHT;
    }

    public boolean isCorner() {
        return type == CORNER_TOP_LEFT || type == CORNER_TOP_RIGHT || type == CORNER_BOTTOM_LEFT || type == CORNER_BOTTOM_RIGHT;
    }

    public boolean isInner() {
        return type == INNER_TOP_LEFT || type == INNER_TOP_RIGHT || type == INNER_BOTTOM_LEFT || type == INNER_BOTTOM_RIGHT;
    }

}
