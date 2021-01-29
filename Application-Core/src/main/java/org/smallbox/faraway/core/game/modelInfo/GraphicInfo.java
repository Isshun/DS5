package org.smallbox.faraway.core.game.modelInfo;

import com.badlogic.gdx.math.Rectangle;
import org.smallbox.faraway.util.Constant;

public class GraphicInfo {
    public enum Type { NONE, TERRAIN, WALL, DOOR, STRUCTURE, PLANT, ICON }

    public int              spriteId = -1;
    public final String     packageName;
    public final String     path;
    public final String     absolutePath;
    public int              x;
    public int              y;
    public int              width;
    public int              height;
    public int              tileWidth = Constant.TILE_SIZE;
    public int              tileHeight = Constant.TILE_SIZE;
    public Type             type = Type.NONE;
    public Rectangle        textureRect;
    public GraphicRandomizationInfo randomization;
    public GraphicAnimationInfo animation;

    public GraphicInfo(String packageName, String path) {
        this.packageName = packageName;
        this.path = path;
        this.absolutePath = "data" + path;
    }
}
